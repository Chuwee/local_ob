package es.onebox.mgmt.collectives.collectivecodes.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.BaseValidityPeriodDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CreateCollectiveCodeRequest implements Serializable {

    private static final long serialVersionUID = -5315644685016572041L;

    @NotNull(message = "Collective code can not be null")
    private String code;
    private String key;
    @JsonProperty("usage_limit")
    @NotNull(message = "usage limit can not be null")
    @Min(value = 0, message = "usage limit must be equal or above 0")
    private Integer usageLimit;
    @JsonProperty("validity_period")
    private BaseValidityPeriodDTO validityPeriod;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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
