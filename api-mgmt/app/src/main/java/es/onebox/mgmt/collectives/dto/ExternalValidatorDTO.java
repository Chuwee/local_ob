package es.onebox.mgmt.collectives.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class ExternalValidatorDTO implements Serializable{

    private static final long serialVersionUID = -7735995303009397883L;

    @JsonProperty("external_validator_name")
    private String externalValidatorName;

    @JsonProperty("external_validator_authentication")
    private CollectiveValidatorAuthentication externalValidatorAuthentication;
    @JsonProperty("external_validator_properties")
    private Map<String,Object> externalValidatorProperties;

    public String getExternalValidatorName() {
        return externalValidatorName;
    }

    public void setExternalValidatorName(String externalValidatorName) {
        this.externalValidatorName = externalValidatorName;
    }

    public Map<String, Object> getExternalValidatorProperties() {
        return externalValidatorProperties;
    }

    public void setExternalValidatorProperties(Map<String, Object> externalValidatorProperties) {
        this.externalValidatorProperties = externalValidatorProperties;
    }

    public CollectiveValidatorAuthentication getExternalValidatorAuthentication() {
        return externalValidatorAuthentication;
    }

    public void setExternalValidatorAuthentication(CollectiveValidatorAuthentication externalValidatorAuthentication) {
        this.externalValidatorAuthentication = externalValidatorAuthentication;
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