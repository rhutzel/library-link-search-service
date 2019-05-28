package com.rhutzel.librarylink.server.repository;

import com.rhutzel.librarylink.server.entity.Requisition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RequisitionRepository {
    private Logger logger = LogManager.getLogger(RequisitionRepository.class);

    @Autowired
    ReactiveMongoTemplate template;

    public Flux<Requisition> findAll() {
        return template.find(new Query().with(new Sort(Sort.Direction.ASC, "postedDate")), Requisition.class);
    }

    public Flux<Requisition> insertAll(Flux<Requisition> requisitions) {
        return requisitions.buffer(10)
                .doOnNext(chunk -> logger.info(String.format("Inserting %d record(s)...", chunk.size())))
                .flatMap(chunk -> template.insertAll(chunk));
    }

    public Mono<Void> deleteAll() {
        return template.dropCollection(Requisition.class);
    }

}
