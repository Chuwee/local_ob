package es.onebox.flc.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 2750023630788526316L;

    private ProductType type;
    @JsonProperty("session_id")
    private Integer sessionId;
    @JsonProperty("event_id")
    private Integer eventId;
    @JsonProperty("group_id")
    private Long groupId;
    //Keep state when there is a booking in progress or product has been refunded
    @JsonProperty("related_product_state")
    private ProductState relatedProductState;
    @JsonProperty("related_booking_code")
    private String relatedBookingCode;
    @JsonProperty("related_refund_code")
    private String relatedRefundCode;

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public ProductState getRelatedProductState() {
        return relatedProductState;
    }

    public void setRelatedProductState(ProductState relatedProductState) {
        this.relatedProductState = relatedProductState;
    }

    public String getRelatedBookingCode() {
        return relatedBookingCode;
    }

    public void setRelatedBookingCode(String relatedBookingCode) {
        this.relatedBookingCode = relatedBookingCode;
    }

    public String getRelatedRefundCode() {
        return relatedRefundCode;
    }

    public void setRelatedRefundCode(String relatedRefundCode) {
        this.relatedRefundCode = relatedRefundCode;
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
