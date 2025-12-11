/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * @author rcarrillo
 */
public class DateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime date;
    @JsonProperty("timezone")
    private TimeZoneDTO timeZone;

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public TimeZoneDTO getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZoneDTO timeZone) {
        this.timeZone = timeZone;
    }
}
