package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class SettingsSessionsSearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 772380746480705288L;

    @JsonProperty("smart_booking")
    private SessionSmartBookingDTO smartBookingDTO;

    @JsonProperty("enable_orphan_seats")
    private Boolean enableOrphanSeats;

    public SessionSmartBookingDTO getSmartBookingDTO() {
        return smartBookingDTO;
    }

    public void setSmartBookingDTO(SessionSmartBookingDTO smartBookingDTO) {
        this.smartBookingDTO = smartBookingDTO;
    }

    public Boolean getEnableOrphanSeats() {
        return enableOrphanSeats;
    }

    public void setEnableOrphanSeats(Boolean enableOrphanSeats) {
        this.enableOrphanSeats = enableOrphanSeats;
    }
}
