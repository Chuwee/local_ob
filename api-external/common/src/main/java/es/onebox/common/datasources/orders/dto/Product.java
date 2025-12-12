package es.onebox.common.datasources.orders.dto;

import es.onebox.common.datasources.orders.enums.EventType;
import es.onebox.common.datasources.orders.enums.ProductState;
import es.onebox.common.datasources.orders.enums.ProductType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private ProductType type;

    private Integer shardId;
    private Long sessionId;
    private Boolean sessionFinalDate;
    private Long eventId;
    private Long eventEntityId;
    private Long venueId;
    private Long venueEntityId;

    private EventType eventType;

    private Long groupId;
    private Integer groupAssistantsVariation;
    private Integer groupAccompanistsVariation;

    private ProductState relatedProductState;
    //References from booking/issue product to preorder orderCode to be purchased
    private String relatedBookingCode;
    //References from original purchased product to order code refunded
    private String relatedRefundCode;
    //Refers to secondary market purchase order related
    private String relatedSecPurchaseCode;
    private String previousPurchaseCode;

    private OrderPrice price;

    private OrderTicketData ticketData;
    private OrderPromotion promotions;
    private OrderProductAdditionalData additionalData;

    public OrderPrice getPrice() {
        return price;
    }

    public void setPrice(OrderPrice price) {
        this.price = price;
    }

    public OrderTicketData getTicketData() {
        return ticketData;
    }

    public void setTicketData(OrderTicketData ticketData) {
        this.ticketData = ticketData;
    }

    public OrderPromotion getPromotions() {
        return promotions;
    }

    public void setPromotions(OrderPromotion promotions) {
        this.promotions = promotions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public Integer getShardId() {
        return shardId;
    }

    public void setShardId(Integer shardId) {
        this.shardId = shardId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getSessionFinalDate() {
        return sessionFinalDate;
    }

    public void setSessionFinalDate(Boolean sessionFinalDate) {
        this.sessionFinalDate = sessionFinalDate;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEventEntityId() {
        return eventEntityId;
    }

    public void setEventEntityId(Long eventEntityId) {
        this.eventEntityId = eventEntityId;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public Long getVenueEntityId() {
        return venueEntityId;
    }

    public void setVenueEntityId(Long venueEntityId) {
        this.venueEntityId = venueEntityId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Integer getGroupAssistantsVariation() {
        return groupAssistantsVariation;
    }

    public void setGroupAssistantsVariation(Integer groupAssistantsVariation) {
        this.groupAssistantsVariation = groupAssistantsVariation;
    }

    public Integer getGroupAccompanistsVariation() {
        return groupAccompanistsVariation;
    }

    public void setGroupAccompanistsVariation(Integer groupAccompanistsVariation) {
        this.groupAccompanistsVariation = groupAccompanistsVariation;
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

    public String getRelatedSecPurchaseCode() {
        return relatedSecPurchaseCode;
    }

    public void setRelatedSecPurchaseCode(String relatedSecPurchaseCode) {
        this.relatedSecPurchaseCode = relatedSecPurchaseCode;
    }

    public String getPreviousPurchaseCode() {
        return previousPurchaseCode;
    }

    public void setPreviousPurchaseCode(String previousPurchaseCode) {
        this.previousPurchaseCode = previousPurchaseCode;
    }

    public OrderProductAdditionalData getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(OrderProductAdditionalData additionalData) {
        this.additionalData = additionalData;
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
