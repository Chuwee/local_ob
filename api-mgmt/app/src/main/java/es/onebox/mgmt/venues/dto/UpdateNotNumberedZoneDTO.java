package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.NameDTO;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class UpdateNotNumberedZoneDTO extends NameDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("quota_id")
    private Integer quotaId;

    private Integer capacity;

    @Min(value = 1L, message = "view_id must be equal or greater than 1")
    @JsonProperty("view_id")
    private Integer viewId;

    @Min(value = 0L, message = "order must be equal or greater than 0")
    private Long order;

    public Integer getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Integer quotaId) {
        this.quotaId = quotaId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getViewId() {
        return viewId;
    }

    public void setViewId(Integer viewId) {
        this.viewId = viewId;
    }

    public @Min(value = 0L, message = "order must be equal or greater than 0") Long getOrder() {
        return order;
    }

    public void setOrder(@Min(value = 0L, message = "order must be equal or greater than 0") Long order) {
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
