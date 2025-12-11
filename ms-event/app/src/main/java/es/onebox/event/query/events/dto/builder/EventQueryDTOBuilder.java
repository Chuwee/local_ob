/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.query.events.dto.builder;

import es.onebox.event.events.enums.EventType;
import es.onebox.event.query.events.dto.Entity;
import es.onebox.event.query.events.dto.EventQueryDTO;
import es.onebox.event.query.events.dto.Rate;
import es.onebox.event.query.events.dto.Sale;
import es.onebox.event.query.events.dto.Venue;

import java.util.List;

/**
 * @author ignasi
 */
public class EventQueryDTOBuilder {

    private Long id;
    private String name;
    private List<Rate> rates;
    private List<Sale> sales;
    private EventType type;
    private Entity entity;
    private Entity promoter;
    private List<Venue> venues;
    private String promoterRef;

    private EventQueryDTOBuilder() {
    }

    public static EventQueryDTOBuilder builder() {
        return new EventQueryDTOBuilder();
    }

    public EventQueryDTO build() {
        EventQueryDTO eventQueryDTO = new EventQueryDTO();
        eventQueryDTO.setId(id);
        eventQueryDTO.setName(name);
        eventQueryDTO.setRates(rates);
        eventQueryDTO.setSales(sales);
        eventQueryDTO.setType(type);
        eventQueryDTO.setEntity(entity);
        eventQueryDTO.setPromoter(promoter);
        eventQueryDTO.setVenues(venues);
        eventQueryDTO.setPromoterRef(promoterRef);

        return eventQueryDTO;
    }

    public EventQueryDTOBuilder id(final Long value) {
        this.id = value;
        return this;
    }

    public EventQueryDTOBuilder name(final String value) {
        this.name = value;
        return this;
    }

    public EventQueryDTOBuilder rates(final List<Rate> value) {
        this.rates = value;
        return this;
    }

    public EventQueryDTOBuilder sales(final List<Sale> value) {
        this.sales = value;
        return this;
    }

    public EventQueryDTOBuilder type(final EventType value) {
        this.type = value;
        return this;
    }

    public EventQueryDTOBuilder entity(final Entity value) {
        this.entity = value;
        return this;
    }

    public EventQueryDTOBuilder promoter(final Entity value) {
        this.promoter = value;
        return this;
    }

    public EventQueryDTOBuilder venues(final List<Venue> value) {
        this.venues = value;
        return this;
    }

    public EventQueryDTOBuilder promoterRef(final String value) {
        this.promoterRef = value;
        return this;
    }

}
