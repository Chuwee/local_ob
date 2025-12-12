package es.onebox.channels.catalog;

import es.onebox.common.datasources.common.dto.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class CatalogEvents implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Metadata metadata;
    private final List<CatalogEvent> events;

    public CatalogEvents(Metadata metadata, List<CatalogEvent> events) {
        this.metadata = metadata;
        this.events = events;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public List<CatalogEvent> getEvents() {
        return events;
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
