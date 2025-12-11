package es.onebox.event.catalog.dto.promotion;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CatalogPromotionCollective implements Serializable {

    @Serial
    private static final long serialVersionUID = -8264934674482421833L;

    private String name;
    private Long id;
    private CatalogPromotionCollectiveValidationMethod validationMethod;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CatalogPromotionCollectiveValidationMethod getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(CatalogPromotionCollectiveValidationMethod validationMethod) {
        this.validationMethod = validationMethod;
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
