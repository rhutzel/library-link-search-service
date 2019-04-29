package com.rhutzel.librarylink.server.controller;

import com.rhutzel.librarylink.server.service.IngestService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class IngestHandler {
    private Logger logger = LogManager.getLogger(IngestHandler.class);

    @Autowired
    IngestService ingestService;

    public Mono<ServerResponse> ingest(ServerRequest request) {
        if (request.remoteAddress().isPresent()) {
            logger.info(String.format("getContext() %s", request.remoteAddress().get().getHostString()));
        }

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build(ingestService.ingest());
    }

}
