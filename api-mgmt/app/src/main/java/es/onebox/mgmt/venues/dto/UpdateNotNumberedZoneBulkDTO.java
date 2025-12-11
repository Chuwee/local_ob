package es.onebox.mgmt.venues.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UpdateNotNumberedZoneBulkDTO extends UpdateNotNumberedZoneDTO {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "id is mandatory")
    @Min(value = 1L, message = "id must be above 0")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
