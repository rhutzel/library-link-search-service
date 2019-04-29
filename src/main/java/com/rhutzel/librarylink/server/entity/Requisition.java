package com.rhutzel.librarylink.server.entity;

// import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;

public class Requisition {

    // @Id
    public String id;
    public String positionType;
    public LocalDate postedDate;
    public String title;
    public String description;
    public List<String> keywords;

    public Requisition(String id, LocalDate postedDate, String title, String description) {
        this.id = id;
        this.postedDate = postedDate;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    public LocalDate getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDate postedDate) {
        this.postedDate = postedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

}
