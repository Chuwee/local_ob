package es.onebox.mgmt.datasources.ms.collective.dto.request;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Optional;

public class MsUpdateCollectiveCodesBulkUnifiedData implements Serializable {

    private static final long serialVersionUID = -5315644685016573041L;

    private Integer usageLimit;
    private Optional<ZonedDateTime> startDate;
    private Optional<ZonedDateTime> endDate;

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Optional<ZonedDateTime> getStartDate() {
        return startDate;
    }

    public void setStartDate(Optional<ZonedDateTime> startDate) {
        this.startDate = startDate;
    }

    public Optional<ZonedDateTime> getEndDate() {
        return endDate;
    }

    public void setEndDate(Optional<ZonedDateTime> endDate) {
        this.endDate = endDate;
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
