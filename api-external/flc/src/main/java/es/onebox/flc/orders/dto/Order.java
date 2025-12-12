package es.onebox.flc.orders.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.utils.TimeZoneResolver;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Order implements DateConvertible, Serializable {

    @Serial
    private static final long serialVersionUID = -3696279462639128613L;

    private String code;
    private OrderType type;
    private OrderState state;
    @JsonProperty("channel_id")
    private Integer channelId;
    @JsonProperty("order_date")
    private ZonedDateTime orderDate;
    @JsonProperty("products_number")
    private Integer productsNumber;
    private List<Long> groups;
    private List<Product> products;
    @JsonProperty("related_original_code")
    private String relatedOriginalCode;
    @JsonProperty("related_modication_code")
    private String relatedModicationCode;
    @JsonIgnore
    private String timeZone;
    @JsonProperty("session_groups")
    private Map<Integer, List<Long>> sessionGroups;
    @JsonProperty("event_sessions")
    private Map<Integer, Set<Integer>> eventSessions;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public ZonedDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(ZonedDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getProductsNumber() {
        return productsNumber;
    }

    public void setProductsNumber(Integer productsNumber) {
        this.productsNumber = productsNumber;
    }

    public List<Long> getGroups() {
        return groups;
    }

    public void setGroups(List<Long> groups) {
        this.groups = groups;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getRelatedOriginalCode() {
        return relatedOriginalCode;
    }

    public void setRelatedOriginalCode(String relatedOriginalCode) {
        this.relatedOriginalCode = relatedOriginalCode;
    }

    public String getRelatedModicationCode() {
        return relatedModicationCode;
    }

    public void setRelatedModicationCode(String relatedModicationCode) {
        this.relatedModicationCode = relatedModicationCode;
    }

    public Map<Integer, List<Long>> getSessionGroups() {
        return sessionGroups;
    }

    public void setSessionGroups(Map<Integer, List<Long>> sessionGroups) {
        this.sessionGroups = sessionGroups;
    }

    public Map<Integer, Set<Integer>> getEventSessions() {
        return eventSessions;
    }

    public void setEventSessions(Map<Integer, Set<Integer>> eventSessions) {
        this.eventSessions = eventSessions;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public void convertDates() {
        if (orderDate != null) {
            orderDate = TimeZoneResolver.applyTimeZone(orderDate, timeZone);
        }
    }
}
