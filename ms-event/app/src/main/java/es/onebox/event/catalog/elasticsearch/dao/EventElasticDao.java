package es.onebox.event.catalog.elasticsearch.dao;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.elasticsearch.dao.AbstractElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.properties.EventElasticProperty;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.springframework.stereotype.Repository;

@Repository
public class EventElasticDao extends AbstractElasticDao<EventData> {

    public ObjectMapper getMapper() {
        return elasticSearchObjectMapper;
    }

    public EventData get(Long eventId) {
        String eventKey = EventDataUtils.getEventKey(eventId);
        return findByID(eventKey, eventKey);
    }

    public long deleteAllRelatedToEvent(Long eventId) {
        BoolQuery.Builder routingQuery = QueryBuilders.bool()
                .must(QueryBuilders.term(term -> term
                        .field(EventElasticProperty.ROUTING.getProperty())
                        .value(EventDataUtils.getEventKey(eventId))));
        return delete(routingQuery);
    }

}
