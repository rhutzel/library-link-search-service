package com.rhutzel.librarylink.server.controller;

import com.rhutzel.librarylink.server.entity.Requisition;
import com.rhutzel.librarylink.server.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class SearchHandler {

    @Autowired
    CacheService cacheService;

    public Mono<ServerResponse> search(ServerRequest request) {
        LocalDate sixMonthsAgo = LocalDate.now().minus(6, ChronoUnit.MONTHS);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON).body(
                        cacheService.retrieveCache(request.queryParam("includes"), request.queryParam("excludes"))
                                .filter(requisition -> requisition.getPostedDate().isAfter(sixMonthsAgo))
                                .filter(requisition -> cacheService.filterPositionType(
                                        requisition, request.queryParam("fullTime"),
                                        request.queryParam("partTime"))),
                Requisition.class);
    }

}
