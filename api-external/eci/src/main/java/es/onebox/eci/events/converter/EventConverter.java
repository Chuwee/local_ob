package es.onebox.eci.events.converter;

import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.dto.ChannelEventEntity;
import es.onebox.eci.events.dto.Event;
import es.onebox.eci.events.dto.Organizer;
import es.onebox.eci.events.dto.Sponsor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventConverter {

    private EventConverter() {
    }

    public static List<Event> convert(List<ChannelEventDetail> channelEventDetails) {
        if (channelEventDetails == null) {
            return Collections.emptyList();
        }

        return channelEventDetails
                .stream()
                .map(EventConverter::convert)
                .collect(Collectors.toList());
    }

    public static Event convert(ChannelEventDetail eventDetail) {
        Event event = new Event();

        event.setIdentifier(String.valueOf(eventDetail.getId()));
        event.setName(eventDetail.getName());
        if (eventDetail.getCategory() != null && eventDetail.getCategory().getCustom() != null) {
            event.setSubcategory(eventDetail.getCategory().getCustom().getDescription());
            if (eventDetail.getCategory().getParent() != null) {
                event.setCategory(eventDetail.getCategory().getParent().getDescription());
            }
        }
        event.setPerformer(StringUtils.EMPTY);
        event.setOrganizer(getOrganizer(eventDetail.getPromoter())); // organizer is our "productor"
        event.setSponsor(getSponsor(eventDetail.getEntity())); // sponsor is our entity promoter

        return event;
    }

    public static Organizer getOrganizer(ChannelEventEntity entity) {
        Organizer organizer = new Organizer();
        organizer.setIdentifier(String.format("O%s", entity.getId()));
        return organizer;
    }

    public static Sponsor getSponsor(ChannelEventEntity entity) {
        Sponsor sponsor = new Sponsor();
        sponsor.setIdentifier(String.format("S%s", entity.getId()));
        return sponsor;
    }
}
