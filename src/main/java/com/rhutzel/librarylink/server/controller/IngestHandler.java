package com.rhutzel.librarylink.server.controller;

import com.rhutzel.librarylink.server.service.IngestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class IngestHandler {

    @Autowired
    IngestService ingestService;

    public Mono<ServerResponse> ingest(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build(ingestService.ingest());
    }

}
