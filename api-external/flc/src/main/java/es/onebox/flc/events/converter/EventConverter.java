package es.onebox.flc.events.converter;

import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.EventsDTO;
import es.onebox.common.datasources.ms.event.enums.EventStatus;
import es.onebox.flc.events.dto.Event;
import es.onebox.flc.events.dto.EventState;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventConverter {
    public static List<Event> convert(EventsDTO eventsDTO, Map<Integer, Map<Integer, List<Integer>>> attributes) {
        return eventsDTO.getData()
                .stream()
                .map(eventDTO -> convert(eventDTO, attributes))
                .collect(Collectors.toList());
    }

    public static Event convert(EventDTO eventDTO, Map<Integer, Map<Integer, List<Integer>>> attributes) {
        Event event = new Event();

        event.setId(eventDTO.getId().intValue());
        event.setName(eventDTO.getName());
        event.setState(getState(eventDTO.getStatus()));
        if (eventDTO.getDate() != null && eventDTO.getDate().getStart() != null) {
            event.setStartDate(eventDTO.getDate().getStart().getDate());
            event.setTimeZone(eventDTO.getDate().getStart().getTimeZone().getOlsonId());
        }
        if (eventDTO.getDate() != null && eventDTO.getDate().getEnd() != null) {
            event.setEndDate(eventDTO.getDate().getEnd().getDate());
        }
        if (eventDTO.getSalesGoalTickets() != null) {
            event.setSalesGoal(Double.valueOf(eventDTO.getSalesGoalTickets()));
        }
        event.setTicketsGoal(eventDTO.getSalesGoalTickets());
        if (attributes != null) {
            event.setAttributeValuesMap(attributes.get(event.getId()));
        }
        event.setExternalReferenceCode(eventDTO.getPromoterReference());

        return event;
    }

    private static EventState getState(EventStatus eventStatus) {
        return EventState.get(eventStatus.getId());
    }
}
