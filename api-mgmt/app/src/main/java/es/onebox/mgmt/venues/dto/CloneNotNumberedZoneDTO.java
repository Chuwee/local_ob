package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CloneNotNumberedZoneDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "name is mandatory")
    private String name;
    @Min(value = 1L, message = "sector_id must be equal or greater than 1")
    @JsonProperty("sector_id")
    private Long sectorId;
    @Min(value = 1L, message = "view_id must be equal or greater than 1")
    @JsonProperty("view_id")
    private Long viewId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public Long getViewId() {
        return viewId;
    }

    public void setViewId(Long viewId) {
        this.viewId = viewId;
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
