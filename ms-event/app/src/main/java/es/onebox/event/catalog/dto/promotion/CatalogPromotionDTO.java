package es.onebox.event.catalog.dto.promotion;

import es.onebox.event.promotions.enums.PromotionStatus;
import es.onebox.event.promotions.enums.PromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CatalogPromotionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2820104153543850505L;

    private Long id;
    private String name;
    private PromotionType type;
    private PromotionStatus status;
    private CatalogPromotionCommunicationElementsDTO communicationElements;
    private CatalogPromotionRestrictionsDTO restrictions;
    private Boolean active;
    private CatalogPromotionCollective collective;
    private Boolean selfManaged;
    private PromotionUsageConditionsDTO usageConditions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public CatalogPromotionCommunicationElementsDTO getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(CatalogPromotionCommunicationElementsDTO communicationElements) {
        this.communicationElements = communicationElements;
    }

    public CatalogPromotionRestrictionsDTO getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(CatalogPromotionRestrictionsDTO restrictions) {
        this.restrictions = restrictions;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public CatalogPromotionCollective getCollective() {
        return collective;
    }

    public void setCollective(CatalogPromotionCollective collective) {
        this.collective = collective;
    }

    public Boolean getSelfManaged() { return selfManaged; }

    public void setSelfManaged(Boolean selfManaged) { this.selfManaged = selfManaged; }

    public PromotionUsageConditionsDTO getUsageConditions() {
        return usageConditions;
    }

    public void setUsageConditions(PromotionUsageConditionsDTO usageConditions) {
        this.usageConditions = usageConditions;
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
