package es.onebox.event.catalog.elasticsearch.dto;

import es.onebox.elasticsearch.annotation.ElasticRepository;
import es.onebox.elasticsearch.dao.ElasticDocument;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

@ElasticRepository(indexName = EventDataUtils.EVENT_INDEX, queryLimit = 500000)
public abstract class BaseEventData implements ElasticDocument, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;

    private JoinField join = new JoinField();

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JoinField getJoin() {
        return join;
    }

    public void setJoin(JoinField join) {
        this.join = join;
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
