package es.onebox.mgmt.datasources.ms.ticket.dto.availability;

import java.io.Serializable;

public class NotNumberedZone implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long notNumberedZoneId;
    private String notNumberedZoneName;
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
