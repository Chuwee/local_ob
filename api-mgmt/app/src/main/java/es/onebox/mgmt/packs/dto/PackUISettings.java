package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class PackUISettings implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("show_date")
    private Boolean showDate;
    @JsonProperty("show_date_time")
    private Boolean showDateTime;
    @JsonProperty("show_main_venue")
    private Boolean showMainVenue;
    @JsonProperty("show_main_date")
    private Boolean showMainDate;

    public Boolean getShowDate() {
        return showDate;
    }

    public void setShowDate(Boolean showDate) {
        this.showDate = showDate;
    }

    public Boolean getShowDateTime() {
        return showDateTime;
    }

    public void setShowDateTime(Boolean showDateTime) {
        this.showDateTime = showDateTime;
    }

    public Boolean getShowMainVenue() {
        return showMainVenue;
    }

    public void setShowMainVenue(Boolean showMainVenue) {
        this.showMainVenue = showMainVenue;
    }

    public Boolean getShowMainDate() {
        return showMainDate;
    }

    public void setShowMainDate(Boolean showMainDate) {
        this.showMainDate = showMainDate;
    }
}
