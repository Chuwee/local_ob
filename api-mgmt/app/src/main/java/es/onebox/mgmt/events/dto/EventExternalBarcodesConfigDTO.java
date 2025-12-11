package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class EventExternalBarcodesConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "allowed can not be null")
    private Boolean allowed;

    @JsonProperty("fair_code")
    private String fairCode;

    @JsonProperty("fair_edition")
    private String fairEdition;

    public Boolean getAllowed() {
        return allowed;
    }

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
    }

    public String getFairCode() {
        return fairCode;
    }

    public void setFairCode(String fairCode) {
        this.fairCode = fairCode;
    }

    public String getFairEdition() {
        return fairEdition;
    }

    public void setFairEdition(String fairEdition) {
        this.fairEdition = fairEdition;
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
