package es.onebox.event.catalog.dto.filter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelCatalogTypeFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = -7543684169532369514L;

    private Long agencyId;
    private Boolean forceRootChannel;

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Boolean getForceRootChannel() {
        return forceRootChannel;
    }

    public void setForceRootChannel(Boolean forceRootChannel) {
        this.forceRootChannel = forceRootChannel;
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
