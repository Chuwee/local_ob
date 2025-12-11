package es.onebox.mgmt.collectives.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CollectiveDetailDTO extends CollectiveDTO {

    private static final long serialVersionUID = -7459667351359623214L;

    @JsonProperty("external_validator")
    private ExternalValidatorDTO externalValidator;
    @JsonProperty("generic")
    private Boolean isGeneric;

    public ExternalValidatorDTO getExternalValidator() {
        return externalValidator;
    }

    public void setExternalValidator(ExternalValidatorDTO externalValidator) {
        this.externalValidator = externalValidator;
    }

    public Boolean getGeneric() {
        return isGeneric;
    }

    public void setGeneric(Boolean generic) {
        isGeneric = generic;
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