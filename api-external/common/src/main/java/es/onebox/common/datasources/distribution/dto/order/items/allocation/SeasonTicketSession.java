package es.onebox.common.datasources.distribution.dto.order.items.allocation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class SeasonTicketSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("session_id")
    private Long sessionId;
    private String title;
    private String subtitle;
    @JsonProperty("session_starting_date")
    private ZonedDateTime sessionStartingDate;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public ZonedDateTime getSessionStartingDate() {
        return sessionStartingDate;
    }

    public void setSessionStartingDate(ZonedDateTime sessionStartingDate) {
        this.sessionStartingDate = sessionStartingDate;
    }
}
