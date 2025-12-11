package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CreateNotNumberedZoneDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "name can not be blank")
    private String name;
    @NotNull(message = "capacity is mandatory")
    @Min(value = 1L, message = "capacity must be above 0")
    private Integer capacity;
    @NotNull(message = "sector_id is mandatory")
    @Min(value = 1L, message = "sector_id must be above 0")
    @JsonProperty("sector_id")
    private Long sectorId;
    @Min(value = 1L, message = "view_id must be above 0")
    @JsonProperty("view_id")
    private Long viewId;

    @JsonProperty("quota_counters")
    @Valid
    private List<QuotaCounterNNZCreationDTO> quotaCounters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
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

    public List<QuotaCounterNNZCreationDTO> getQuotaCounters() {
        return quotaCounters;
    }

    public void setQuotaCounters(List<QuotaCounterNNZCreationDTO> quotaCounters) {
        this.quotaCounters = quotaCounters;
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
