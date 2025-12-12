package es.onebox.common.datasources.ms.ticket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class TicketsUpdateRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7941390745086186091L;
    @JsonProperty("session_id")
    private Long sessionId;
    @JsonProperty("blocking_reason_id")
    private Long blockingReasonId;
    private TicketStatus status;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getBlockingReasonId() {
        return blockingReasonId;
    }

    public void setBlockingReasonId(Long blockingReasonId) {
        this.blockingReasonId = blockingReasonId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
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
