package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.oneboxinvoicing.enums.OneboxInvoiceType;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class GenerateOneboxInvoiceRequest implements Serializable{

    private static final long serialVersionUID = 2L;

    private Long userId;
    private Long operatorId;
    private List<Long> entitiesId;
    private List<Long> eventIds;
    private String entityCode;
    private String email;
    private ZonedDateTime from;
    private ZonedDateTime to;
    private OneboxInvoiceType orderPerspective;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public List<Long> getEntitiesId() {
        return entitiesId;
    }

    public void setEntitiesId(List<Long> entitiesId) {
        this.entitiesId = entitiesId;
    }

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ZonedDateTime getFrom() {
        return from;
    }

    public void setFrom(ZonedDateTime from) {
        this.from = from;
    }

    public ZonedDateTime getTo() {
        return to;
    }

    public void setTo(ZonedDateTime to) {
        this.to = to;
    }

    public OneboxInvoiceType getOrderPerspective() {
        return orderPerspective;
    }

    public void setOrderPerspective(OneboxInvoiceType orderPerspective) {
        this.orderPerspective = orderPerspective;
    }
}
