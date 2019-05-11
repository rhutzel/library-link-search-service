package com.rhutzel.librarylink.server.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

import com.rhutzel.librarylink.server.controller.SearchHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SearchRouter {

    @Bean
    public RouterFunction<ServerResponse> searchRoutes(SearchHandler handler) {
        return RouterFunctions
                .route(GET("/search").and(accept(MediaType.APPLICATION_JSON)), handler::search);
    }

}
