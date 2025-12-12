package es.onebox.common.datasources.ms.promotion.dto;

import java.io.Serializable;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PromotionLimitDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    private Boolean enabled;
    @Min(value = 1, message = "Limit must be greater than 0")
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

    public void setLimit(Integer limit) {
        this.limit = limit;
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
