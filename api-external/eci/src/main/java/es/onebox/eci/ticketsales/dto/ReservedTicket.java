package es.onebox.eci.ticketsales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ReservedTicket implements Serializable {

    @Serial
    private static final long serialVersionUID = 7451444863044747814L;

    @JsonProperty("sku_eci")
    private String skuEci;
    @JsonProperty("sku_platform")
    private String skuPlatform;
    @JsonProperty("ticket_type")
    private String ticketType;
    @JsonProperty("ticketed_seats")
    private List<Seat> seats;

    public String getSkuEci() {
        return skuEci;
    }

    public void setSkuEci(String skuEci) {
        this.skuEci = skuEci;
    }

    public String getSkuPlatform() {
        return skuPlatform;
    }

    public void setSkuPlatform(String skuPlatform) {
        this.skuPlatform = skuPlatform;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
