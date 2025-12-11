package es.onebox.event.catalog.elasticsearch.dto;

import es.onebox.event.promotions.dto.PromotionCommElements;
import es.onebox.event.promotions.enums.PromotionStatus;
import es.onebox.event.promotions.enums.PromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * @author ignasi
 */
public class Promotion implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long promotionTemplateId;
    private Long eventPromotionTemplateId;
    private Long eventId;
    private String name;
    private PromotionType type;
    private PromotionStatus status;
    private PromotionCommElements communicationElements;
    private Boolean active;
    private Boolean selfManaged;
    private Boolean restrictiveAccess;

    public Long getPromotionTemplateId() {
        return promotionTemplateId;
    }

    public void setPromotionTemplateId(Long promotionTemplateId) {
        this.promotionTemplateId = promotionTemplateId;
    }

    public Long getEventPromotionTemplateId() {
        return eventPromotionTemplateId;
    }

    public void setEventPromotionTemplateId(Long eventPromotionTemplateId) {
        this.eventPromotionTemplateId = eventPromotionTemplateId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public PromotionCommElements getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(PromotionCommElements communicationElements) {
        this.communicationElements = communicationElements;
    }

    public Boolean getSelfManaged() { return selfManaged; }

    public void setSelfManaged(Boolean selfManaged) { this.selfManaged = selfManaged; }

    public Boolean getRestrictiveAccess() {
        return restrictiveAccess;
    }

    public void setRestrictiveAccess(Boolean restrictiveAccess) {
        this.restrictiveAccess = restrictiveAccess;
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
