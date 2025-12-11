package es.onebox.mgmt.common.promotions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class PromotionMaxLimitDTO extends PromotionLimitDTO implements Serializable {

    private static final long serialVersionUID = 1714700346347035755L;

    private Integer current;

    public PromotionMaxLimitDTO(Boolean enabled, Integer limit, Integer current) {
        super(enabled, limit);
        this.current = current;
    }

    public PromotionMaxLimitDTO(Integer current) {
        this.current = current;
    }

    public PromotionMaxLimitDTO() {
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
