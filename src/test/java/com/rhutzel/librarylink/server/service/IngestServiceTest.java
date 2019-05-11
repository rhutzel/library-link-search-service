package com.rhutzel.librarylink.server.service;

import org.junit.Test;
import org.springframework.util.StreamUtils;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.Month;

public class IngestServiceTest {

    IngestService service = new IngestService();

    @Test
    public void testExtractRequisitionsFromHtml() throws IOException {
        String testHtml = StreamUtils.copyToString(
                getClass().getClassLoader().getResourceAsStream("sample-results.html"),
                Charset.forName("UTF-8"));

        StepVerifier.create(service.extractRequisitionsFromHtml(testHtml))
                .expectNextMatches(requisitions -> requisitions.size() == 2
                        && requisitions.get(0).getTitle().equals("Test Title 1")
                        && requisitions.get(0).getDescriptionHtml().equals("Test Body 1")
                        && requisitions.get(0).getDescriptionLowerCaseText().equals("test body 1")
                        && requisitions.get(0).getPostedDate().equals(LocalDate.of(2001, Month.JANUARY, 1))
                        && requisitions.get(1).getTitle().equals("Test Title 2")
                        && requisitions.get(1).getDescriptionHtml().equals("Test Body 2")
                        && requisitions.get(1).getDescriptionLowerCaseText().equals("test body 2")
                        && requisitions.get(1).getPostedDate().equals(LocalDate.of(2002, Month.FEBRUARY, 2))
                ).verifyComplete();
    }

}
