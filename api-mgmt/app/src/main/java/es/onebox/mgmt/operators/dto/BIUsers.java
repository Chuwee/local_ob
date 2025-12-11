package es.onebox.mgmt.operators.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public record BIUsers(@JsonProperty("basic_licences_limit") @Min(0) Integer basicBIUsersLimit,
                      @JsonProperty("advanced_licences_limit") @Min(0) Integer advancedBIUsersLimit)
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}


