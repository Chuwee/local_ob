package es.onebox.mgmt.realms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class RoleDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    public RoleDTO() {
    }

    public RoleDTO(String code) {
        this.code = code;
    }

    @NotBlank
    @JsonProperty("code")
    private String code;
    @JsonProperty("permissions")
    private List<String> permissions;
    @JsonProperty("additional_properties")
    private AdditionalPropertiesDTO additionalProperties;

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getPermissions() {
        return permissions;
    }
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public AdditionalPropertiesDTO getAdditionalProperties() {
        return additionalProperties;
    }
    public void setAdditionalProperties(AdditionalPropertiesDTO additionalProperties) {
        this.additionalProperties = additionalProperties;
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
