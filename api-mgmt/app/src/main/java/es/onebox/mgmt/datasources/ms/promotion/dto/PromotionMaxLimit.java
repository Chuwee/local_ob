package es.onebox.mgmt.datasources.ms.promotion.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PromotionMaxLimit extends PromotionLimit implements Serializable {

    private static final long serialVersionUID = 2457200878428015740L;

    private Integer current;

    public PromotionMaxLimit() {
    }

    public PromotionMaxLimit(Integer current) {
        this.current = current;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
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
