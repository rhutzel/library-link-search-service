package com.rhutzel.librarylink.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.Objects;

public class Requisition {

    // @Id
    public String id;
    public String positionType;
    public LocalDate postedDate;
    public String title;
    public String descriptionHtml;
    @JsonIgnore
    public String descriptionLowerCaseText;

    public Requisition(String id, LocalDate postedDate, String title, String descriptionHtml, String descriptionLowerCaseText) {
        this.id = id;
        this.postedDate = postedDate;
        this.title = title;
        this.descriptionHtml = descriptionHtml;
        this.descriptionLowerCaseText = descriptionLowerCaseText;
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

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public String getDescriptionLowerCaseText() {
        return descriptionLowerCaseText;
    }

    public void setDescriptionText(String descriptionLowerCaseText) {
        this.descriptionLowerCaseText = descriptionLowerCaseText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Requisition that = (Requisition) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Requisition{" +
                "id='" + id + '\'' +
                ", postedDate=" + postedDate +
                ", title='" + title + '\'' +
                '}';
    }
}
