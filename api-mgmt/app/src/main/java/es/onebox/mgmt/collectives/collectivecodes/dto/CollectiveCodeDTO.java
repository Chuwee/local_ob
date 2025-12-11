package es.onebox.mgmt.collectives.collectivecodes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.collectives.dto.ValidationMethod;
import es.onebox.mgmt.common.BaseValidityPeriodDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CollectiveCodeDTO implements Serializable {

    private static final long serialVersionUID = -5315644685016572041L;

    private String code;
    private String key;
    @JsonProperty("validation_method")
    private ValidationMethod validationMethod;
    @JsonProperty("validity_period")
    private BaseValidityPeriodDTO validityPeriod;
    private CollectiveCodeUsageDTO usage;

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

    public ValidationMethod getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(ValidationMethod validationMethod) {
        this.validationMethod = validationMethod;
    }

    public BaseValidityPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(BaseValidityPeriodDTO validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public CollectiveCodeUsageDTO getUsage() {
        return usage;
    }

    public void setUsage(CollectiveCodeUsageDTO usage) {
        this.usage = usage;
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
