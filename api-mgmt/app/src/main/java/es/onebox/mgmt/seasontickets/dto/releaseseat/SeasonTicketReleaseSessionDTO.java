package es.onebox.mgmt.seasontickets.dto.releaseseat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class SeasonTicketReleaseSessionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3009961798354342480L;

    private Long id;
    private String name;
    @JsonProperty("start_date")
    private ZonedDateTime startDate;

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

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }
}