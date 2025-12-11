package es.onebox.event.datasources.ms.ticket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class TicketFilter extends BaseRequestFilter {

    private static final long serialVersionUID = 1L;

    @JsonProperty("session_id")
    private Long sessionId;

    @JsonProperty("id")
    private List<Long> id;

    @JsonProperty("status")
    private List<TicketStatus> status;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<Long> getId() {
        return id;
    }

    public void setId(List<Long> id) {
        this.id = id;
    }

    public List<TicketStatus> getStatus() {
        return status;
    }

    public void setStatus(List<TicketStatus> status) {
        this.status = status;
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
