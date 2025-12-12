package es.onebox.common.datasources.orderitems.dto.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ItemTicketValidation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1693834301888469439L;

    private ZonedDateTime date;
    private String status;
    @JsonProperty("session_id")
    private Long sessionId;

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
