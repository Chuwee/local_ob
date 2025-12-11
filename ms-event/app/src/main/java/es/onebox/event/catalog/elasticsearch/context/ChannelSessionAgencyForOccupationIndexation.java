package es.onebox.event.catalog.elasticsearch.context;

import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ChannelSessionAgencyForOccupationIndexation extends ForOccupationIndexation<ChannelSessionAgency> {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long agencyId;

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
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
