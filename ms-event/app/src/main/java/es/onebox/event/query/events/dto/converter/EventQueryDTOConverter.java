/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.query.events.dto.converter;

import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventRate;
import es.onebox.event.catalog.elasticsearch.dto.Promotion;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.query.events.dto.Entity;
import es.onebox.event.query.events.dto.EventQueryDTO;
import es.onebox.event.query.events.dto.Rate;
import es.onebox.event.query.events.dto.Sale;
import es.onebox.event.query.events.dto.Venue;
import es.onebox.event.query.events.dto.builder.EventQueryDTOBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ignasi
 */
public class EventQueryDTOConverter {

    private EventQueryDTOConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static EventQueryDTO from(Event event) {
        EventQueryDTOBuilder builder = EventQueryDTOBuilder.builder();
        builder.id(event.getEventId())
                .name(event.getEventName())
                .type(EventType.byId(event.getEventType().intValue()))
                .promoterRef(event.getPromoterRef());

        Entity entity = new Entity();
        entity.setId(event.getEntity().getId().longValue());
        entity.setName(event.getEntity().getName());
        builder.entity(entity);

        Entity promoter = new Entity();
        promoter.setId(event.getPromoter().getId().longValue());
        promoter.setName(event.getPromoter().getName());
        builder.promoter(promoter);

        builder.rates(fillRates(event.getRates()))
                .sales(fillSales(event.getPromotions()));
        builder.venues(fillVenues(event.getVenues()));
        return builder.build();

    }

    private static List<Rate> fillRates(List<EventRate> rates) {
        if (CollectionUtils.isNotEmpty(rates)) {
            return rates.stream().map(r -> {
                Rate rate = new Rate();
                rate.setId(r.getId());
                rate.setName(r.getName());
                rate.setDefaultRate(r.getDefaultRate());

                return rate;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private static List<Sale> fillSales(List<Promotion> promotions) {
        if (CollectionUtils.isNotEmpty(promotions)) {
            return promotions.stream().map(p -> {
                Sale sale = new Sale();
                sale.setTemplateId(p.getPromotionTemplateId());
                sale.setEventTemplateId(p.getEventPromotionTemplateId());
                sale.setName(p.getName());
                sale.setType(p.getType());

                return sale;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private static List<Venue> fillVenues(List<es.onebox.event.catalog.elasticsearch.dto.Venue> venues) {
        if (CollectionUtils.isNotEmpty(venues)) {
            return venues.stream()
                    .map(v -> {
                        Venue venue = new Venue();
                        venue.setId(v.getId());
                        venue.setName(v.getName());
                        Entity venueEntity = new Entity();
                        venueEntity.setId(v.getEntityId());
                        venueEntity.setName(v.getEntityName());
                        venue.setEntity(venueEntity);

                        return venue;
                    }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
