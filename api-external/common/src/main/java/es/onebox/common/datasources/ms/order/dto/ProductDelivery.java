package es.onebox.common.datasources.ms.order.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductDelivery implements Serializable {

    private static final long serialVersionUID = -7248696302212177674L;

    private Long sessionId;
    private String sessionName;
    private Long deliveryPointId;
    private String deliveryPointName;
    private ZonedDateTime deliveryFrom;
    private ZonedDateTime deliveryTo;
    private ProductDeliveryType type;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Long getDeliveryPointId() {
        return deliveryPointId;
    }

    public void setDeliveryPointId(Long deliveryPointId) {
        this.deliveryPointId = deliveryPointId;
    }

    public String getDeliveryPointName() {
        return deliveryPointName;
    }

    public void setDeliveryPointName(String deliveryPointName) {
        this.deliveryPointName = deliveryPointName;
    }

    public ZonedDateTime getDeliveryFrom() {
        return deliveryFrom;
    }

    public void setDeliveryFrom(ZonedDateTime deliveryFrom) {
        this.deliveryFrom = deliveryFrom;
    }

    public ZonedDateTime getDeliveryTo() {
        return deliveryTo;
    }

    public void setDeliveryTo(ZonedDateTime deliveryTo) {
        this.deliveryTo = deliveryTo;
    }

    public ProductDeliveryType getType() {
        return type;
    }

    public void setType(ProductDeliveryType type) {
        this.type = type;
    }
}
