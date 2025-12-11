package es.onebox.mgmt.collectives.collectivecodes.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.BaseValidityPeriodDTO;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateCollectiveCodeRequest implements Serializable {

    private static final long serialVersionUID = 9012182546946273650L;

    @JsonProperty("usage_limit")
    @Min(value = 0, message = "usage limit must be equal or above 0")
    private Integer usageLimit;
    @JsonProperty("validity_period")
    private BaseValidityPeriodDTO validityPeriod;

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public BaseValidityPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(BaseValidityPeriodDTO validityPeriod) {
        this.validityPeriod = validityPeriod;
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
