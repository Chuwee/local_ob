package es.onebox.mgmt.channels.catalog.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;


public class ChannelEventUpdateDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ChannelEventCatalogDataDTO catalog;

    public ChannelEventCatalogDataDTO getCatalog() {
        return catalog;
    }

    public void setCatalog(ChannelEventCatalogDataDTO catalog) {
        this.catalog = catalog;
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
