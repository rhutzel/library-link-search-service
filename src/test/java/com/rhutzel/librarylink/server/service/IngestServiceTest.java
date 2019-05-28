package com.rhutzel.librarylink.server.service;

import com.rhutzel.librarylink.server.entity.Requisition;
import org.junit.Assert;
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

    @Test
    public void testCategorizesInternInTitleAsPartTime() {
        Requisition testRequisition1 = new Requisition("1", LocalDate.now(), "Summer Intern Program", "", "");
        Requisition testRequisition2 = new Requisition("1", LocalDate.now(), "Summer Internet Program", "", "");
        Requisition testRequisition3 = new Requisition("1", LocalDate.now(), "Summer Intern,Program", "", "");
        Requisition testRequisition4 = new Requisition("1", LocalDate.now(), "Full-time Summer Intern Program", "", "");

        service.annotateRequisition(testRequisition1);
        service.annotateRequisition(testRequisition2);
        service.annotateRequisition(testRequisition3);
        service.annotateRequisition(testRequisition4);

        Assert.assertEquals("Part-Time", testRequisition1.getPositionType());
        Assert.assertNull(testRequisition2.getPositionType());
        Assert.assertEquals("Part-Time", testRequisition3.getPositionType());
        Assert.assertEquals("Part-Time", testRequisition4.getPositionType());
    }

}
