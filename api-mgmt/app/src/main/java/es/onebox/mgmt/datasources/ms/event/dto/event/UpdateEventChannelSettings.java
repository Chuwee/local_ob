package es.onebox.mgmt.datasources.ms.event.dto.event;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class UpdateEventChannelSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean useEventDates;

    private Boolean releaseEnabled;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime releaseDate;

    private Boolean saleEnabled;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime saleStartDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime saleEndDate;

    private Boolean bookingEnabled;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime bookingStartDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime bookingEndDate;

    private Boolean secondaryMarketEnabled;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime secondaryMarketStartDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime secondaryMarketEndDate;


    public Boolean getUseEventDates() {
        return useEventDates;
    }

    public void setUseEventDates(Boolean useEventDates) {
        this.useEventDates = useEventDates;
    }

    public Boolean getReleaseEnabled() {
        return releaseEnabled;
    }

    public void setReleaseEnabled(Boolean releaseEnabled) {
        this.releaseEnabled = releaseEnabled;
    }

    public ZonedDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(ZonedDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Boolean getSaleEnabled() {
        return saleEnabled;
    }

    public void setSaleEnabled(Boolean saleEnabled) {
        this.saleEnabled = saleEnabled;
    }

    public ZonedDateTime getSaleStartDate() {
        return saleStartDate;
    }

    public void setSaleStartDate(ZonedDateTime saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    public ZonedDateTime getSaleEndDate() {
        return saleEndDate;
    }

    public void setSaleEndDate(ZonedDateTime saleEndDate) {
        this.saleEndDate = saleEndDate;
    }

    public Boolean getBookingEnabled() {
        return bookingEnabled;
    }

    public void setBookingEnabled(Boolean bookingEnabled) {
        this.bookingEnabled = bookingEnabled;
    }

    public ZonedDateTime getBookingStartDate() {
        return bookingStartDate;
    }

    public void setBookingStartDate(ZonedDateTime bookingStartDate) {
        this.bookingStartDate = bookingStartDate;
    }

    public ZonedDateTime getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(ZonedDateTime bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public Boolean getSecondaryMarketEnabled() {
        return secondaryMarketEnabled;
    }

    public void setSecondaryMarketEnabled(Boolean secondaryMarketEnabled) {
        this.secondaryMarketEnabled = secondaryMarketEnabled;
    }

    public ZonedDateTime getSecondaryMarketStartDate() {
        return secondaryMarketStartDate;
    }

    public void setSecondaryMarketStartDate(ZonedDateTime secondaryMarketStartDate) {
        this.secondaryMarketStartDate = secondaryMarketStartDate;
    }

    public ZonedDateTime getSecondaryMarketEndDate() {
        return secondaryMarketEndDate;
    }

    public void setSecondaryMarketEndDate(ZonedDateTime secondaryMarketEndDate) {
        this.secondaryMarketEndDate = secondaryMarketEndDate;
    }
}
