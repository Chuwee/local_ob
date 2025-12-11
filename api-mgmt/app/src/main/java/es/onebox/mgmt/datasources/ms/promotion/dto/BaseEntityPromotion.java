package es.onebox.mgmt.datasources.ms.promotion.dto;

import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionActivationStatus;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class BaseEntityPromotion implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private PromotionActivationStatus status;
    private PromotionType type;

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

    public PromotionActivationStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionActivationStatus status) {
        this.status = status;
    }

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
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
