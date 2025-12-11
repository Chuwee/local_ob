package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.dto.RateTextsDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CreatePackRateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("name")
    @Size(max = 50, message = "Rate name may have up to 50 chars")
    @NotNull
    private String name;

    @JsonProperty("default")
    @NotNull
    private Boolean isDefault;

    @JsonProperty("restrictive_access")
    @NotNull
    private Boolean restrictiveAccess;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Boolean getRestrictiveAccess() {
        return restrictiveAccess;
    }

    public void setRestrictiveAccess(Boolean restrictiveAccess) {
        this.restrictiveAccess = restrictiveAccess;
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
