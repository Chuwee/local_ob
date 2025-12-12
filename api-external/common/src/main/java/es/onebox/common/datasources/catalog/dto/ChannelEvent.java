package es.onebox.common.datasources.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.catalog.dto.common.Dates;
import es.onebox.common.datasources.catalog.dto.common.Venue;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String reference;
    @JsonProperty("supra_event")
    private Boolean supraEvent;
    private ChannelEventTexts texts;
    private ChannelEventImages images;
    private ChannelEventCategory category;
    private Dates date;
    private ChannelEventLanguage language;
    private List<Venue> venues;
    private TourInfo tour;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Boolean getSupraEvent() {
        return supraEvent;
    }

    public void setSupraEvent(Boolean supraEvent) {
        this.supraEvent = supraEvent;
    }

    public ChannelEventTexts getTexts() {
        return texts;
    }

    public void setTexts(ChannelEventTexts texts) {
        this.texts = texts;
    }

    public ChannelEventImages getImages() {
        return images;
    }

    public void setImages(ChannelEventImages images) {
        this.images = images;
    }

    public ChannelEventCategory getCategory() {
        return category;
    }

    public void setCategory(ChannelEventCategory category) {
        this.category = category;
    }

    public Dates getDate() {
        return date;
    }

    public void setDate(Dates date) {
        this.date = date;
    }

    public ChannelEventLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ChannelEventLanguage language) {
        this.language = language;
    }

    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }

    public TourInfo getTour() {
        return tour;
    }

    public void setTour(TourInfo tour) {
        this.tour = tour;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
