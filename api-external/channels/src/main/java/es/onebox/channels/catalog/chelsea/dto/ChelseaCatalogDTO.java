package es.onebox.channels.catalog.chelsea.dto;

import es.onebox.channels.catalog.ChannelCatalog;
import es.onebox.channels.catalog.generic.dto.CatalogMetadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChelseaCatalogDTO implements ChannelCatalog, Serializable {

    private static final long serialVersionUID = -4654368500586859001L;

    private CatalogMetadata metadata;
    private List<ChelseaSessionDTO> data;

    public CatalogMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(CatalogMetadata metadata) {
        this.metadata = metadata;
    }

    public List<ChelseaSessionDTO> getData() {
        return data;
    }

    public void setData(List<ChelseaSessionDTO> data) {
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
