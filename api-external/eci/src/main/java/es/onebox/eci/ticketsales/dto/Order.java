package es.onebox.eci.ticketsales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = -88612161096273415L;

    @JsonProperty("order_date")
    private ZonedDateTime orderDate;
    @JsonProperty("channel_platform")
    private String channelPlatform;
    private Provider provider;
    private Customer customer;
    private Reservation reservation;
    @JsonProperty("sale_operation")
    private SaleOperation saleOperation;
    @JsonProperty("ordered_items")
    private List<OrderedItem> orderedItems;
    private List<Payment> payments;

    public ZonedDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(ZonedDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getChannelPlatform() {
        return channelPlatform;
    }

    public void setChannelPlatform(String channelPlatform) {
        this.channelPlatform = channelPlatform;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public SaleOperation getSaleOperation() {
        return saleOperation;
    }

    public void setSaleOperation(SaleOperation saleOperation) {
        this.saleOperation = saleOperation;
    }

    public List<OrderedItem> getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(List<OrderedItem> orderedItems) {
        this.orderedItems = orderedItems;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
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
