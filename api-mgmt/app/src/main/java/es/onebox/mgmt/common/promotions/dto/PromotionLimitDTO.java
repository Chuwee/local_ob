package es.onebox.mgmt.common.promotions.dto;

import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class PromotionLimitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    @Min(value = 1, message = "ticket limits must be greater than 0")
    private Integer limit;

    public PromotionLimitDTO() {
    }

    public PromotionLimitDTO(Boolean enabled, Integer limit) {
        this.enabled = enabled;
        this.limit = limit;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer tickets) {
        this.limit = tickets;
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
