package com.rhutzel.librarylink.server.service;

import com.rhutzel.librarylink.server.entity.Requisition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IngestService {
    private Logger logger = LogManager.getLogger(IngestService.class);

    @Value("${mocking}")
    private String useMocking;

    @Autowired
    private CacheService cacheService;

    public Mono<Void> ingest() {
        Flux.range(0, 1000)
                .delayElements(Duration.ofSeconds(2))
                .concatMap(pageNumber -> useMocking.toLowerCase().equals("true")
                        ? retrieveMockedPage(pageNumber)
                        : retrieveLivePage(pageNumber))
                .concatMap(html -> extractRequisitionsFromHtml(html))
                .takeWhile(requisitions -> requisitions.size() > 0)
                .flatMapIterable(requisitions -> requisitions)
                .map(requisitions -> annotateRequisition(requisitions))
                .doOnNext(requisition -> { logger.debug(requisition.toString()); })
                .collectList()
                .doOnNext(requisitions -> cacheService.fillCache(requisitions))
                .subscribe();

        return Mono.empty();
    }

    public Mono<String> retrieveMockedPage(int pageNumber) {
        return Mono.just(String.format("mocks/results-page-%d.html", pageNumber))
                .doOnNext(path -> {
                    logger.info(String.format("Retrieving [%s]...", path));
                })
                .map(path -> new ClassPathResource(path))
                .map(resource -> {
                    try {
                        return StreamUtils.copyToString(resource.getInputStream(), Charset.forName("UTF-8"));
                    } catch (IOException ex) {
                        logger.error(String.format("Failed to parse [%s].", resource.getPath()));
                        return "";
                    }
                });
    }

    public Mono<String> retrieveLivePage(int pageNumber) {
        WebClient client = WebClient.create("https://librarylinknj.org");
        String uri = String.format("/jobs?page=%d", pageNumber);
        logger.debug(String.format("Retrieving [%s]...", uri));
        return client.get().uri(uri).accept(MediaType.TEXT_HTML)
                .retrieve().bodyToMono(String.class);
    }

    public Mono<List<Requisition>> extractRequisitionsFromHtml(String html) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

        Mono<List<Requisition>> blockingWrapper = Mono.fromCallable(() -> {
            Document doc = Jsoup.parse(html);
            List<Requisition> requisitions = new ArrayList<>();

            logger.info("Doc: " + doc.title());

            if (doc.select("table.views-table").size() == 0) {
                logger.debug("Detected empty page.");
                return requisitions;
            }

            Elements rows = doc.select("tr");
            rows.stream().forEach(row -> {
                List<String> titles = row.select("td.views-field-title").eachText();
                List<LocalDate> createDates = row.select("td.views-field-created")
                        .eachText().stream()
                        .map(created -> LocalDate.parse(created, dateFormatter))
                        .collect(Collectors.toList());
                Elements bodies = row.select("td.views-field-body");

                if (bodies.size() > 0 && titles.size() > 0 && createDates.size() > 0) {
                    requisitions.add(new Requisition(
                            UUID.randomUUID().toString(),
                            createDates.size() > 0 ? createDates.get(0) : null,
                            titles.size() > 0 ? titles.get(0) : null,
                            bodies.size() > 0 ? bodies.get(0).html() : null,
                            bodies.size() > 0 ? bodies.get(0).text().toLowerCase() : null
                    ));
                }
            });

            return requisitions;
        }).subscribeOn(Schedulers.elastic());

        // Postpone call until there is a subscription.
        return Mono.defer(() -> blockingWrapper);
    }

    Requisition annotateRequisition(Requisition requisition) {
        String[] partTimeIndicators = new String[] { "p/t" , "part-time" };
        String[] fullTimeIndicators = new String[] { "f/t" , "full-time" };
        String title = requisition.getTitle().toLowerCase();
        String description = requisition.getDescriptionLowerCaseText();

        if (title.startsWith("ft ") || Arrays.stream(fullTimeIndicators)
                .anyMatch(indicator -> title.contains(indicator) || description.contains(indicator))) {
            requisition.setPositionType("Full-Time");
        }

        if (Arrays.stream(partTimeIndicators).anyMatch(indicator -> title.contains(indicator) || description.contains(indicator))) {
            requisition.setPositionType("Part-Time");
        }

        return requisition;
    }

}
