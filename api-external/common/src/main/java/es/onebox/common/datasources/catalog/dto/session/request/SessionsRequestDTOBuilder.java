package es.onebox.common.datasources.catalog.dto.session.request;

import java.util.List;

public final class SessionsRequestDTOBuilder {
    List<Long> eventIds;
    List<Long> sessionIds;
    Long limit;
    Long offset;

    private SessionsRequestDTOBuilder() {
    }

    public static SessionsRequestDTOBuilder builder() {
        return new SessionsRequestDTOBuilder();
    }

    public SessionsRequestDTOBuilder eventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
        return this;
    }

    public SessionsRequestDTOBuilder sessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
        return this;
    }

    public SessionsRequestDTOBuilder limit(Long limit) {
        this.limit = limit;
        return this;
    }

    public SessionsRequestDTOBuilder offset(Long offset) {
        this.offset = offset;
        return this;
    }

    public SessionsRequestDTO build() {
        SessionsRequestDTO sessionsRequestDTO = new SessionsRequestDTO();
        sessionsRequestDTO.setEventIds(eventIds);
        sessionsRequestDTO.setSessionIds(sessionIds);
        sessionsRequestDTO.setLimit(limit);
        sessionsRequestDTO.setOffset(offset);
        return sessionsRequestDTO;
    }
}
