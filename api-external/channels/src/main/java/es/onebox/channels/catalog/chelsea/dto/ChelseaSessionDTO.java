package es.onebox.channels.catalog.chelsea.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.channels.catalog.generic.dto.EventDTO;
import es.onebox.channels.catalog.generic.dto.SessionDatesDTO;
import es.onebox.channels.catalog.generic.dto.SessionPriceDTO;
import es.onebox.channels.catalog.generic.dto.VenueDTO;
import es.onebox.common.datasources.catalog.dto.common.Promotion;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ChelseaSessionDTO implements Serializable {

    private static final long serialVersionUID = -8651433868581303187L;

    private Long id;
    private String name;
    private String reference;
    @JsonProperty("sold_out")
    private Boolean soldOut;
    private Map<String,String> url;
    private SessionDatesDTO date;
    private SessionPriceDTO price;
    private List<Promotion> promotions;
    private VenueDTO venue;
    private ChelseaEventDTO event;
    private List<ChelseaZoneCapacityDTO> zones;

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

    public ChelseaEventDTO getEvent() {
        return event;
    }

    public void setEvent(ChelseaEventDTO event) {
        this.event = event;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public List<ChelseaZoneCapacityDTO> getZones() {
        return zones;
    }

    public void setZones(List<ChelseaZoneCapacityDTO> zones) {
        this.zones = zones;
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
