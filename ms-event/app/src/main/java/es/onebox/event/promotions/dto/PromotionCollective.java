package es.onebox.event.promotions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.promotions.enums.CollectiveType;
import es.onebox.event.promotions.enums.CollectiveValidationType;

import java.io.Serializable;

public class PromotionCollective implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private CollectiveType type;
    private CollectiveValidationType validationType;
    private Boolean exclusiveSale;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CollectiveType getType() {
        return type;
    }

    public void setType(CollectiveType type) {
        this.type = type;
    }

    public CollectiveValidationType getValidationType() {
        return validationType;
    }

    public void setValidationType(CollectiveValidationType validationType) {
        this.validationType = validationType;
    }

    public Boolean getExclusiveSale() {
        return exclusiveSale;
    }

    public void setExclusiveSale(Boolean exclusiveSale) {
        this.exclusiveSale = exclusiveSale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
