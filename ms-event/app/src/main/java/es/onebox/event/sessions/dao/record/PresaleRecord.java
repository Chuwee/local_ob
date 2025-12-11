package es.onebox.event.sessions.dao.record;

import es.onebox.jooq.cpanel.tables.records.CpanelPreventaRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class PresaleRecord extends CpanelPreventaRecord {

    @Serial
    private static final long serialVersionUID = -5575010505008611940L;

    private List<Long> channelIds;
    private List<Long> customerTypeIds;
    private Long points;

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public List<Long> getCustomerTypeIds() {
        return customerTypeIds;
    }

    public void setCustomerTypeIds(List<Long> customerTypeIds) {
        this.customerTypeIds = customerTypeIds;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
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
