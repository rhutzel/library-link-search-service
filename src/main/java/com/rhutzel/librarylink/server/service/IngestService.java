package com.rhutzel.librarylink.server.service;

import com.rhutzel.librarylink.server.entity.Requisition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class IngestService {
    private Logger logger = LogManager.getLogger(IngestService.class);

    @Value("${mocking}")
    private String useMocking;

    public Mono<Void> ingest() {
        if (useMocking.toLowerCase().equals("true")) {
            Flux.range(0, 1000)
                    .concatMap(pageNumber -> retrieveMockedPage(pageNumber))
                    .concatMap(html -> extractRequisitionsFromHtml(html))
                    .takeWhile(requisitions -> {
                        logger.info("requisitions size: " + requisitions.size());
                        return requisitions.size() > 0;
                    }).subscribe();
        }

        return Mono.empty();
    }

    public Mono<String> retrieveMockedPage(int pageNumber) {
        /*
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
         */
        return Mono.just(String.valueOf(pageNumber));
    }

    public Mono<List<Requisition>> extractRequisitionsFromHtml(String html) {
        Mono<List<Requisition>> blockingWrapper = Mono.fromCallable(() -> {
            Document doc = Jsoup.parse(html);
            List<Requisition> requisitions = new ArrayList<>();
            logger.info("Doc: " + doc.title());

            if (Integer.valueOf(html) < 1) {
                requisitions.add(new Requisition("1", LocalDate.now(), "t1", "d1"));
            }

            return requisitions;
        }).subscribeOn(Schedulers.elastic());

        // Postpone call until there is a subscription.
        return Mono.defer(() -> blockingWrapper);
    }

}
