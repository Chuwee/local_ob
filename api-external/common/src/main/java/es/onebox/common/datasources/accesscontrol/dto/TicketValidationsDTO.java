package es.onebox.common.datasources.accesscontrol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class TicketValidationsDTO implements Serializable {

    private static final long serialVersionUID = -2106598545935740392L;

    @JsonProperty("status")
    private TicketValidationStatus validationStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime date;

    public TicketValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(TicketValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
}
