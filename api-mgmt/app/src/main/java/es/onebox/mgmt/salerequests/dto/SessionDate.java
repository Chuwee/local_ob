package es.onebox.mgmt.salerequests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class SessionDate implements Serializable {

    private static final long serialVersionUID = 1L;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime start;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime end;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime publication;
    @JsonProperty("sales_start")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime salesStart;
    @JsonProperty("sales_end")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime salesEnd;
    @JsonProperty("booking_start")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime bookingStart;
    @JsonProperty("booking_end")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime bookingEnd;

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public ZonedDateTime getPublication() {
        return publication;
    }

    public void setPublication(ZonedDateTime publication) {
        this.publication = publication;
    }

    public ZonedDateTime getSalesStart() {
        return salesStart;
    }

    public void setSalesStart(ZonedDateTime salesStart) {
        this.salesStart = salesStart;
    }

    public ZonedDateTime getSalesEnd() {
        return salesEnd;
    }

    public void setSalesEnd(ZonedDateTime salesEnd) {
        this.salesEnd = salesEnd;
    }

    public ZonedDateTime getBookingStart() {
        return bookingStart;
    }

    public void setBookingStart(ZonedDateTime bookingStart) {
        this.bookingStart = bookingStart;
    }

    public ZonedDateTime getBookingEnd() {
        return bookingEnd;
    }

    public void setBookingEnd(ZonedDateTime bookingEnd) {
        this.bookingEnd = bookingEnd;
    }
}
