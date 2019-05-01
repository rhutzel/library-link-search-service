package com.rhutzel.librarylink.server.service;

import com.rhutzel.librarylink.server.entity.Requisition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class CacheService {
    private Logger logger = LogManager.getLogger(CacheService.class);

    private CopyOnWriteArrayList<Requisition> cache = new CopyOnWriteArrayList<>();

    public void fillCacheFromPersistence() {
        cache.clear();
    }

    public void fillCache(List<Requisition> requisitions) {
        logger.info(String.format("Filling cache with [%d] requisitions...", requisitions.size()));
        cache.addAll(requisitions);
    }

    public void persistCache() {
    }

    public Flux<Requisition> retrieveCache(Optional<String> includeCsv, Optional<String> excludeCsv) {
        List<String> includes = includeCsv.isPresent() && includeCsv.get().length() > 0
                ? Arrays.asList(includeCsv.get().toLowerCase().split(",")) : Collections.emptyList();
        List<String> excludes = excludeCsv.isPresent() && excludeCsv.get().length() > 0
                ? Arrays.asList(excludeCsv.get().toLowerCase().split(",")) : Collections.emptyList();

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
}
