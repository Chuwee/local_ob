package es.onebox.event.catalog.elasticsearch.dto.session;

import es.onebox.elasticsearch.annotation.ElasticRepository;
import es.onebox.event.catalog.elasticsearch.dto.BaseEventData;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

@ElasticRepository(indexName = EventDataUtils.EVENT_INDEX, queryLimit = 500000)
public class SessionData extends BaseEventData {

    @Serial
    private static final long serialVersionUID = 8406713107019876077L;

    private Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
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
