package es.onebox.ms.notification.ie.orderrelease.dto;

import java.io.Serializable;

/**
 * User: grodrigues
 * Date: 12/2/15
 */
public class OrderProductDTO implements Serializable {

    private String barcode;
    private String orderCode;
    private Integer eventId;
    private Integer eventEntityId;
    private Integer sessionId;
    private Integer productType;
    private Integer ticketType;
    private Long groupId;
    private OrderDataDTO orderData;
    private Double finalPrice;
    private Integer promotionAutomaticId;
    private Integer promotionManualId;
    private Integer customerDiscountId;

    public OrderProductDTO() {
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public OrderDataDTO getOrderData() {
        if( orderData == null ){
            orderData = new OrderDataDTO();
        }
        return orderData;
    }

    public void setOrderData(OrderDataDTO orderData) {
        this.orderData = orderData;
    }

    public Integer getEventEntityId() {
        return eventEntityId;
    }

    public void setEventEntityId(Integer eventEntityId) {
        this.eventEntityId = eventEntityId;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Integer getTicketType() {
        return ticketType;
    }

    public void setTicketType(Integer ticketType) {
        this.ticketType = ticketType;
    }

    public Integer getPromotionAutomaticId() {
        return promotionAutomaticId;
    }

    public void setPromotionAutomaticId(Integer promotionAutomaticId) {
        this.promotionAutomaticId = promotionAutomaticId;
    }

    public Integer getPromotionManualId() {
        return promotionManualId;
    }

    public void setPromotionManualId(Integer promotionManualId) {
        this.promotionManualId = promotionManualId;
    }

    public Integer getCustomerDiscountId() {
        return customerDiscountId;
    }

    public void setCustomerDiscountId(Integer customerDiscountId) {
        this.customerDiscountId = customerDiscountId;
    }

    public boolean hasDiscount() {
        boolean hasDiscount = false;
        if(promotionAutomaticId != null || promotionManualId != null || customerDiscountId != null) {
            hasDiscount = true;
        }
        return hasDiscount;
    }

    public static OrderProductDTO from(es.onebox.dal.dto.couch.order.OrderProductDTO product) {
        OrderProductDTO result = new OrderProductDTO();
        result.setBarcode(product.getTicketData().getBarcode());
        result.setEventId(product.getEventId());
        result.setSessionId(product.getSessionId());
        result.setProductType(product.getType().getId());
        result.setTicketType(product.getTicketData().getTicketType().getId());
        result.setGroupId(product.getGroupId());
        result.setEventEntityId(product.getEventEntityId());
        result.setFinalPrice(product.getPrice().getFinalPrice());
        if(product.getPromotions().getAutomatic() != null) {
            result.setPromotionAutomaticId(product.getPromotions().getAutomatic().getId());
        }
        if(product.getPromotions().getPromotion() != null) {
            result.setPromotionManualId(product.getPromotions().getPromotion().getId());
        }
        if(product.getPromotions().getDiscount() != null) {
            result.setCustomerDiscountId(product.getPromotions().getDiscount().getId());
        }
        return result;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
