package es.onebox.channels.catalog.eci.dto;

import es.onebox.channels.catalog.ChannelCatalog;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ECICatalogDTO implements ChannelCatalog, Serializable {

    private static final long serialVersionUID = 1L;

    private ECICatalogMetadata metadata;
    private List<ECIEventDTO> records;

    public ECICatalogMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ECICatalogMetadata metadata) {
        this.metadata = metadata;
    }

    public List<ECIEventDTO> getRecords() {
        return records;
    }

    public void setRecords(List<ECIEventDTO> records) {
        this.records = records;
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
