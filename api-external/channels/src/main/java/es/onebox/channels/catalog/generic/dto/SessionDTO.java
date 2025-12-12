package es.onebox.channels.catalog.generic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.catalog.dto.common.Promotion;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SessionDTO implements Serializable {

    private static final long serialVersionUID = -8651433868581303187L;

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("reference")
    private String reference;
    @JsonProperty("sold_out")
    private Boolean soldOut;
    @JsonProperty("url")
    private Map<String,String> url;
    @JsonProperty("date")
    private SessionDatesDTO date;
    @JsonProperty("price")
    private SessionPriceDTO price;
    @JsonProperty("promotions")
    private List<Promotion> promotions;
    @JsonProperty("venue")
    private VenueDTO venue;
    @JsonProperty("description")
    private Map<String, String> description;
    @JsonProperty("event")
    private EventDTO event;

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

    public Boolean getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    public Map<String, String> getUrl() {
        return url;
    }

    public void setUrl(Map<String, String> url) {
        this.url = url;
    }

    public SessionPriceDTO getPrice() {
        return price;
    }

    public void setPrice(SessionPriceDTO price) {
        this.price = price;
    }

    public VenueDTO getVenue() {
        return venue;
    }

    public SessionDatesDTO getDate() {
        return date;
    }

    public void setDate(SessionDatesDTO date) {
        this.date = date;
    }

    public void setVenue(VenueDTO venue) {
        this.venue = venue;
    }

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
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
