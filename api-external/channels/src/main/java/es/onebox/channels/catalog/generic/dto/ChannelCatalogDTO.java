package es.onebox.channels.catalog.generic.dto;

import es.onebox.channels.catalog.ChannelCatalog;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelCatalogDTO implements ChannelCatalog, Serializable {

    private static final long serialVersionUID = -4790251501714046668L;

    private CatalogMetadata metadata;
    private List<SessionDTO> data;

    public CatalogMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(CatalogMetadata metadata) {
        this.metadata = metadata;
    }

    public List<SessionDTO> getData() {
        return data;
    }

    public void setData(List<SessionDTO> data) {
        this.data = data;
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
