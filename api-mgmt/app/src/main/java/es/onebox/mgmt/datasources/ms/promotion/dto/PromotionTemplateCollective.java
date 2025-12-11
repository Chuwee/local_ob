package es.onebox.mgmt.datasources.ms.promotion.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PromotionTemplateCollective implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private PromotionCollectiveType type;
    private Boolean restrictiveSale;
    private Boolean boxOfficeValidation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PromotionCollectiveType getType() {
        return type;
    }

    public void setType(PromotionCollectiveType type) {
        this.type = type;
    }

    public Boolean getRestrictiveSale() {
        return restrictiveSale;
    }

    public void setRestrictiveSale(Boolean restrictiveSale) {
        this.restrictiveSale = restrictiveSale;
    }

    public Boolean getBoxOfficeValidation() {
        return boxOfficeValidation;
    }

    public void setBoxOfficeValidation(Boolean boxOfficeValidation) {
        this.boxOfficeValidation = boxOfficeValidation;
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
