package es.onebox.mgmt.datasources.ms.promotion.dto;

import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionStatus;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PromotionTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private PromotionStatus status;
    private PromotionType type;
    private PromotionValidityDates validityDates;
    private Boolean presale;

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

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
    }

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }

    public PromotionValidityDates getValidityDates() {
        return validityDates;
    }

    public void setValidityDates(PromotionValidityDates validityDates) {
        this.validityDates = validityDates;
    }

    public Boolean getPresale() {
        return presale;
    }

    public void setPresale(Boolean presale) {
        this.presale = presale;
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
