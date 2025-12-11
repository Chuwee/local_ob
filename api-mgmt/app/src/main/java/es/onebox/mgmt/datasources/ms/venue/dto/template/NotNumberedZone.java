package es.onebox.mgmt.datasources.ms.venue.dto.template;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class NotNumberedZone extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 2L;

    private Integer capacity;
    private Long sectorId;
    private Long viewId;
    private List<QuotaCounter> quotaCounters;
    private Long order;

    public NotNumberedZone() {
    }

    public NotNumberedZone(Long id) {
        super(id);
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

    public List<QuotaCounter> getQuotaCounters() {
        return quotaCounters;
    }

    public void setQuotaCounters(List<QuotaCounter> quotaCounters) {
        this.quotaCounters = quotaCounters;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
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
