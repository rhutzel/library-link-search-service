package com.rhutzel.librarylink.server;

import com.rhutzel.librarylink.server.service.IngestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class IngestTest {

    @MockBean
    private IngestService ingestService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testGetContextRoute() {
        BDDMockito.given(this.ingestService.ingest()).willReturn(Mono.empty());

        webTestClient.get().uri("/ingest").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class).isEqualTo(null);
    }

}
