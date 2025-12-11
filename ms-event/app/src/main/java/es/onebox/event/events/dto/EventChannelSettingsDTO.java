package es.onebox.event.events.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class EventChannelSettingsDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Boolean useEventDates;

    private Boolean releaseEnabled;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime releaseDate;
    private String releaseDateTZ;

    private Boolean saleEnabled;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime saleStartDate;
    private String saleStartDateTZ;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime saleEndDate;
    private String saleEndDateTZ;

    private Boolean bookingEnabled;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime bookingStartDate;
    private String bookingStartDateTZ;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime bookingEndDate;
    private String bookingEndDateTZ;

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

    public String getReleaseDateTZ() {
        return releaseDateTZ;
    }

    public void setReleaseDateTZ(String releaseDateTZ) {
        this.releaseDateTZ = releaseDateTZ;
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

    public String getSaleStartDateTZ() {
        return saleStartDateTZ;
    }

    public void setSaleStartDateTZ(String saleStartDateTZ) {
        this.saleStartDateTZ = saleStartDateTZ;
    }

    public ZonedDateTime getSaleEndDate() {
        return saleEndDate;
    }

    public void setSaleEndDate(ZonedDateTime saleEndDate) {
        this.saleEndDate = saleEndDate;
    }

    public String getSaleEndDateTZ() {
        return saleEndDateTZ;
    }

    public void setSaleEndDateTZ(String saleEndDateTZ) {
        this.saleEndDateTZ = saleEndDateTZ;
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

    public String getBookingStartDateTZ() {
        return bookingStartDateTZ;
    }

    public void setBookingStartDateTZ(String bookingStartDateTZ) {
        this.bookingStartDateTZ = bookingStartDateTZ;
    }

    public ZonedDateTime getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(ZonedDateTime bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public String getBookingEndDateTZ() {
        return bookingEndDateTZ;
    }

    public void setBookingEndDateTZ(String bookingEndDateTZ) {
        this.bookingEndDateTZ = bookingEndDateTZ;
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
