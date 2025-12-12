package es.onebox.common.datasources.catalog.dto.session.request;

import java.util.List;

public final class EventsRequestDTOBuilder {

    private List<Long> eventIds;
    private Long limit;
    private Long offset;

    private EventsRequestDTOBuilder() {
    }

    public static EventsRequestDTOBuilder builder() {
        return new EventsRequestDTOBuilder();
    }

    public EventsRequestDTOBuilder eventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
        return this;
    }

    public EventsRequestDTOBuilder limit(Long limit) {
        this.limit = limit;
        return this;
    }

    public EventsRequestDTOBuilder offset(Long offset) {
        this.offset = offset;
        return this;
    }

    public EventsRequestDTO build() {
        EventsRequestDTO eventsRequestDTO = new EventsRequestDTO();
        eventsRequestDTO.setEventIds(eventIds);
        eventsRequestDTO.setLimit(limit);
        eventsRequestDTO.setOffset(offset);

        return eventsRequestDTO;
    }
}
