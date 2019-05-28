package com.rhutzel.librarylink.server.service;

import com.rhutzel.librarylink.server.entity.Requisition;
import com.rhutzel.librarylink.server.repository.RequisitionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class CacheService {
    private Logger logger = LogManager.getLogger(CacheService.class);
    private CopyOnWriteArrayList<Requisition> cache = new CopyOnWriteArrayList<>();

    @Autowired
    RequisitionRepository requisitionRepository;

    @PostConstruct
    private void init() {
        fillCacheFromPersistence();
    }

    public void fillCacheFromPersistence() {
        logger.info("Filling cache from persistence...");
        cache.clear();
        requisitionRepository.findAll().collectList().subscribe(requisitions -> {
            cache.addAll(requisitions);
            logger.info(String.format("Cache contains [%d] requisitions.", cache.size()));
        });
    }

    public void fillCache(List<Requisition> requisitions) {
        logger.info(String.format("Filling cache with [%d] requisitions...", requisitions.size()));
        this.cache.clear();
        this.cache.addAll(requisitions);
        persistCache();
    }

    public void persistCache() {
        requisitionRepository.deleteAll()
                .thenMany(requisitionRepository.insertAll(Flux.fromIterable(this.cache)))
                .doOnComplete(() -> logger.info("Finished persisting cache.")).subscribe();
    }

    public Flux<Requisition> retrieveCache(Optional<String> includeCsv, Optional<String> excludeCsv) {
        List<String> includes = convertCsvToFilterTerms(includeCsv);
        List<String> excludes = convertCsvToFilterTerms(excludeCsv);

        return Flux.fromIterable(this.cache).filter(requisition -> {
            if (excludes.size() > 0 && excludes.stream().anyMatch(exclude ->
                    requisition.getTitle().toLowerCase().contains(exclude)
                            || requisition.getDescriptionLowerCaseText().contains(exclude))) {
                return false;
            }
            return includes.size() == 0 || (
                    includes.stream().anyMatch(include -> requisition.getTitle().toLowerCase().contains(include)
                            || requisition.getDescriptionLowerCaseText().contains(include))
            );
        });
    }

    public List<String> convertCsvToFilterTerms(Optional<String> csv) {
        if (!csv.isPresent() || csv.get().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(csv.get().split(","))
                .map(term -> term.toLowerCase().trim())
                .collect(Collectors.toList());
    }

    public boolean filterPositionType(Requisition requisition, Optional<String> fullTime, Optional<String> partTime) {
        if (requisition.getPositionType() == null) {
            return true;
        }

        if (fullTime.isPresent()) {
            if (fullTime.get().equals("true") && requisition.getPositionType().equals("Part-Time")) {
                return false;
            }
            if (!fullTime.get().equals("true") && requisition.getPositionType().equals("Full-Time")) {
                return false;
            }
        }
        if (partTime.isPresent()) {
            if (partTime.get().equals("true") && requisition.getPositionType().equals("Full-Time")) {
                return false;
            }
            if (!partTime.get().equals("true") && requisition.getPositionType().equals("Part-Time")) {
                return false;
            }
        }
        return true;
    }
}
