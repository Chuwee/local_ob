package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CreateVenueTemplateRowDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    @NotNull(message = "sector_id must not be null")
    @Min(value = 0, message = "sector_id must be above 0")
    @JsonProperty("sector_id")
    private Integer sectorId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("order")
    private Integer order;

    public Integer getSectorId() {
        return sectorId;
    }

    public void setSectorId(Integer sectorId) {
        this.sectorId = sectorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
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
