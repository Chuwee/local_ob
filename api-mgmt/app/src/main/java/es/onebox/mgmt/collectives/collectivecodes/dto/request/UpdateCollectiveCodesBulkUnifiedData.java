package es.onebox.mgmt.collectives.collectivecodes.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateCollectiveCodesBulkUnifiedData implements Serializable {

    private static final long serialVersionUID = -5315644685016573041L;

    @JsonProperty("usage_limit")
    @Min(value = 0, message = "usage limit must be equal or above 0")
    private Integer usageLimit;
    @JsonProperty("validity_period")
    private BulkUnifiedValidityPeriodDTO validityPeriod;

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public BulkUnifiedValidityPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(BulkUnifiedValidityPeriodDTO validityPeriod) {
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
