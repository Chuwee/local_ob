package es.onebox.event.catalog.elasticsearch.dao;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.FilterAggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.ParentIdQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.onebox.elasticsearch.dao.AbstractElasticDao;
import es.onebox.elasticsearch.dao.Page;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.catalog.elasticsearch.properties.SessionElasticProperty;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.events.enums.SessionState;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.onebox.event.catalog.elasticsearch.utils.EventDataUtils.KEY_SESSION;

@Repository
@Primary
public class SessionElasticDao extends AbstractElasticDao<SessionData> {

    private static final Page NO_PAGE = new Page(0, 0);
    private static final String SESSION_AGG = "SESSION_AGG";
    private static final String SESSION_STATE_FILTER = "SESSION_STATE_FILTER";

    public SessionData get(Long sessionId, Long eventId) {
        return findByID(EventDataUtils.getSessionKey(sessionId), EventDataUtils.getEventKey(eventId));
    }

    public List<Long> getSessions(Long eventId) {
        String eventKey = EventDataUtils.getEventKey(eventId);
        ParentIdQuery.Builder query = QueryBuilders.parentId().type(KEY_SESSION).id(eventKey);

        Aggregation sessions = new Aggregation.Builder()
                .filter(filter -> filter.bool(bool -> bool
                        .mustNot(QueryBuilders.term(term -> term
                                .field(SessionElasticProperty.STATUS.getProperty()).value(SessionState.DELETED.value())))
                ))
                .aggregations(SESSION_AGG, AggregationBuilders.terms(terms -> terms
                        .field(SessionElasticProperty.ID.getProperty())
                        .size(Integer.MAX_VALUE))).build();

        Map<String, Aggregation> aggregations = new HashMap<>();
        aggregations.put(SESSION_STATE_FILTER, sessions);
        SearchResponse<SessionData> response = queryAgg(query.build()._toQuery(), aggregations, NO_PAGE, eventKey);

        List<Long> sessionIds = null;

        FilterAggregate sessionFilterAgg = response.aggregations().get(SESSION_STATE_FILTER).filter();
        Aggregate aggregation = sessionFilterAgg.aggregations().get(SESSION_AGG);
        if (aggregation != null) {
            sessionIds = aggregation
                    .lterms().buckets().array().stream()
                    .map(sessionId -> sessionId.key())
                    .collect(Collectors.toList());
        }

        return sessionIds;
    }

}
