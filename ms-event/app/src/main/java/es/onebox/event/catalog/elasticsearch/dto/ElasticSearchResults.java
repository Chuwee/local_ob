package es.onebox.event.catalog.elasticsearch.dto;

import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ElasticSearchResults<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Metadata metadata;

    private List<T> results;

    public ElasticSearchResults() {
    }

    public ElasticSearchResults(Metadata metadata, List<T> results) {
        this.metadata = metadata;
        this.results = results;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
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
