package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.venue.dto.VenueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class VenueItemPostRequestDTO extends VenueItemRequestDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("entity_id")
    @Positive
    private Long entityId;

    public Long getEntityId() {
        return this.entityId;
    }
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    @NotBlank(message="venue name is mandatory")
    public String getName() {
        return super.getName();
    }

    @NotNull(message = "timezone is mandatory")
    public String getTimezone() {
        return super.getTimezone();
    }

    @NotNull(message="venue capacity is mandatory")
    public Integer getCapacity() {
        return super.getCapacity();
    }

    @NotNull(message= "type is mandatory")
    public VenueType getType() {
        return super.getType();
    }

    @NotNull(message= "country code is mandatory")
    public String getCountryCode() {
        return super.getCountryCode();
    }

    @NotNull(message= "subcountry division code is mandatory")
    public String getCountrySubdivisionCode() {
        return super.getCountrySubdivisionCode();
    }

    @NotBlank(message= "city is mandatory")
    public String getCity() {
        return super.getCity();
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
