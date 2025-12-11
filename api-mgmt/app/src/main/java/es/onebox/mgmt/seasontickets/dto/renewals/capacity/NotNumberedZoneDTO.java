package es.onebox.mgmt.seasontickets.dto.renewals.capacity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class NotNumberedZoneDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("not_numbered_zone_id")
    private Long notNumberedZoneId;
    @JsonProperty("not_numbered_zone_name")
    private String notNumberedZoneName;
    @JsonProperty("available_seats")
    private Long availableSeats;

    public Long getNotNumberedZoneId() {
        return notNumberedZoneId;
    }

    public void setNotNumberedZoneId(Long notNumberedZoneId) {
        this.notNumberedZoneId = notNumberedZoneId;
    }

    public String getNotNumberedZoneName() {
        return notNumberedZoneName;
    }

    public void setNotNumberedZoneName(String notNumberedZoneName) {
        this.notNumberedZoneName = notNumberedZoneName;
    }

    public Long getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Long availableSeats) {
        this.availableSeats = availableSeats;
    }
}
