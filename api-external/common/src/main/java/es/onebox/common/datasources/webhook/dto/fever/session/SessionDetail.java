package es.onebox.common.datasources.webhook.dto.fever.session;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;

@JsonNaming(SnakeCaseStrategy.class)
public class SessionDetail implements Serializable {

    private Long id;
    private String name;
    private SessionStatus status;
    private Long venueId;
    private String venueName;
    private SessionDateFeverDTO date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public SessionDateFeverDTO getDate() {
        return date;
    }

    public void setDate(SessionDateFeverDTO date) {
        this.date = date;
    }
}

