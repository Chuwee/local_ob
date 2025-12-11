package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class CreateSessionDates implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("start")
    private ZonedDateTime startDate;

    @JsonProperty("end")
    private ZonedDateTime endDate;

    @JsonProperty("channels")
    private ZonedDateTime channelsDate;

    @JsonProperty("sales_start")
    private ZonedDateTime salesStartDate;

    @JsonProperty("sales_end")
    private ZonedDateTime salesEndDate;

    @JsonProperty("bookings_start")
    private ZonedDateTime bookingsStartDate;

    @JsonProperty("bookings_end")
    private ZonedDateTime bookingsEndDate;

    @JsonProperty("secondary_market_sale_start")
    private ZonedDateTime secondaryMarketSaleStartDate;

    @JsonProperty("secondary_market_sale_end")
    private ZonedDateTime secondaryMarketSaleEndDate;

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public ZonedDateTime getChannelsDate() {
        return channelsDate;
    }

    public void setChannelsDate(ZonedDateTime channelsDate) {
        this.channelsDate = channelsDate;
    }

    public ZonedDateTime getSalesStartDate() {
        return salesStartDate;
    }

    public void setSalesStartDate(ZonedDateTime salesStartDate) {
        this.salesStartDate = salesStartDate;
    }

    public ZonedDateTime getSalesEndDate() {
        return salesEndDate;
    }

    public void setSalesEndDate(ZonedDateTime salesEndDate) {
        this.salesEndDate = salesEndDate;
    }

    public ZonedDateTime getBookingsStartDate() {
        return bookingsStartDate;
    }

    public void setBookingsStartDate(ZonedDateTime bookingsStartDate) {
        this.bookingsStartDate = bookingsStartDate;
    }

    public ZonedDateTime getBookingsEndDate() {
        return bookingsEndDate;
    }

    public void setBookingsEndDate(ZonedDateTime bookingsEndDate) {
        this.bookingsEndDate = bookingsEndDate;
    }

    public ZonedDateTime getSecondaryMarketSaleStartDate() {
        return secondaryMarketSaleStartDate;
    }

    public void setSecondaryMarketSaleStartDate(ZonedDateTime secondaryMarketSaleStartDate) {
        this.secondaryMarketSaleStartDate = secondaryMarketSaleStartDate;
    }

    public ZonedDateTime getSecondaryMarketSaleEndDate() {
        return secondaryMarketSaleEndDate;
    }

    public void setSecondaryMarketSaleEndDate(ZonedDateTime secondaryMarketSaleEndDate) {
        this.secondaryMarketSaleEndDate = secondaryMarketSaleEndDate;
    }
}
