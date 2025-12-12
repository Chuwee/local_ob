package es.onebox.common.datasources.ms.order.dto;

import es.onebox.common.datasources.ms.order.enums.ProductState;
import es.onebox.common.datasources.ms.order.enums.ProductType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class OrderProductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7055401537248512197L;

    private Long id;
    private ProductType type;
    private Integer sessionId;
    private Integer eventId;
    private Integer eventEntityId;
    private Integer venueId;
    private Long groupId;
    private ProductState relatedProductState;
    private String relatedBookingCode;
    private String relatedRefundCode;
    private EventType eventType;
    private OrderTicketDataDTO ticketData = new OrderTicketDataDTO();
    private OrderProductDataDTO productData;
    private OrderPriceDTO price = new OrderPriceDTO();
    private OrderPromotionDTO promotions = new OrderPromotionDTO();
    private OrderOperationsDTO operations = new OrderOperationsDTO();
    private AttendantHistoryDTO attendant;
    private String simplifiedInvoiceNumber;
    private OrderPackDTO pack;
    private SeasonSessionTransferDTO transferData;
    private String userId;

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

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Integer getEventEntityId() {
        return eventEntityId;
    }

    public void setEventEntityId(Integer eventEntityId) {
        this.eventEntityId = eventEntityId;
    }

    public OrderTicketDataDTO getTicketData() {
        return ticketData;
    }

    public void setTicketData(OrderTicketDataDTO ticketData) {
        this.ticketData = ticketData;
    }

    public OrderProductDataDTO getProductData() {
        return productData;
    }

    public void setProductData(OrderProductDataDTO productData) {
        this.productData = productData;
    }

    public OrderPriceDTO getPrice() {
        return price;
    }

    public void setPrice(OrderPriceDTO price) {
        this.price = price;
    }

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public OrderPromotionDTO getPromotions() {
        return promotions;
    }

    public void setPromotions(OrderPromotionDTO promotions) {
        this.promotions = promotions;
    }

    public AttendantHistoryDTO getAttendant() {
        return attendant;
    }

    public void setAttendant(AttendantHistoryDTO attendant) {
        this.attendant = attendant;
    }

    public String getSimplifiedInvoiceNumber() {
        return simplifiedInvoiceNumber;
    }

    public void setSimplifiedInvoiceNumber(String simplifiedInvoiceNumber) {
        this.simplifiedInvoiceNumber = simplifiedInvoiceNumber;
    }

    public OrderPackDTO getPack() {
        return pack;
    }

    public void setPack(OrderPackDTO pack) {
        this.pack = pack;
    }

    public SeasonSessionTransferDTO getTransferData() {
        return transferData;
    }

    public void setTransferData(SeasonSessionTransferDTO transferData) {
        this.transferData = transferData;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OrderOperationsDTO getOperations() {
        return operations;
    }

    public void setOperations(OrderOperationsDTO operations) {
        this.operations = operations;
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
