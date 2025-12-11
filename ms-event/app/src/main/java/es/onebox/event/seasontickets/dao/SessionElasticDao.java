package es.onebox.event.seasontickets.dao;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.elasticsearch.dao.AbstractElasticDao;
import es.onebox.elasticsearch.dao.Page;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.catalog.elasticsearch.properties.EventElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.SessionElasticProperty;
import es.onebox.event.catalog.elasticsearch.utils.ESBuilder;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SessionResultDTO;
import es.onebox.event.seasontickets.elasticsearch.PaginationUtils;
import es.onebox.event.seasontickets.elasticsearch.SearchSessionsSortUtils;
import es.onebox.event.seasontickets.elasticsearch.SessionsESFilterDecorator;
import es.onebox.event.seasontickets.request.SeasonTicketSessionsEventsFilter;
import es.onebox.event.seasontickets.request.SeasonTicketSessionsSearchFilter;
import es.onebox.event.sessions.dto.SessionStatus;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository("seasonTicketSessionElasticDao")
public class SessionElasticDao extends AbstractElasticDao<SessionData> {

    private static final Page NO_PAGE = new Page(0, 0);
    public static final String DIFFERENT_EVENTS_FILTER = "differentEventsFilter";
    public static final String DIFFERENT_EVENTS_AGGREGATION = "differentEventsAggregation";
    public static final String SESSIONS_ON_SALE_FILTER = "sessionsOnSaleFilter";
    public static final String SESSIONS_ON_SALE_AGGREGATION = "sessionsOnSaleAggregation";
    public static final String ASSIGNED_SESSIONS_FILTER = "assignedSessionsFilter";
    public static final String ASSIGNED_SESSIONS_AGGREGATION = "assignedSessionsAggregation";

    public SearchResponse<SessionData> getSessions(SeasonTicketSessionsSearchFilter filter, SeasonTicketDTO seasonTicketDTO) {
        BoolQuery.Builder query = SessionsESFilterDecorator.prepareSearchSessionsWithFilters(filter, seasonTicketDTO);
        Page page = PaginationUtils.buildPage(filter);
        List<SortOptions> sort = SearchSessionsSortUtils.prepareSort(filter.getSort(), seasonTicketDTO.getSessionId().longValue());

        return query(query.build()._toQuery(), sort, page);
    }

    public SearchResponse<SessionData> getPossibleEventsQuery(SeasonTicketDTO seasonTicketDTO, SeasonTicketSessionsEventsFilter filter) {
        BoolQuery.Builder query = SessionsESFilterDecorator.prepareSearchSessionEvents(seasonTicketDTO, filter);

        SearchRequest searchRequest = SearchRequest.of(r -> r
                .docvalueFields(value -> value.field(EventElasticProperty.ID.getProperty()))
                .docvalueFields(value -> value.field(EventElasticProperty.NAME.getProperty()))
                .query(query.build()._toQuery())
                .source(s -> s.fetch(false))
                .sort(SortOptionsBuilders.field(sort -> sort.field(EventElasticProperty.NAME.getProperty()).order(SortOrder.Desc)))
                .from(filter.getOffset().intValue())
                .size(filter.getLimit().intValue())
        );

        return search(searchRequest);
    }

    public List<SessionResultDTO> getSeasonTicketCandidateSessionsDTO(SearchResponse<SessionData> resultSet) {
        return resultSet.hits().hits().stream().map(h -> elasticSearchObjectMapper.convertValue(
                h.source().getSession(), SessionResultDTO.class)).toList();
    }

    public List<IdNameDTO> getSeasonTicketSessionEventsDTO(SearchResponse<SessionData> resultSet) {
        return resultSet.hits().hits().stream().map(h -> {
                    JsonData eventIdField = h.fields().get(EventElasticProperty.ID.getProperty());
                    Integer eventId = (Integer) eventIdField.to(List.class).get(0);
                    String eventName = "";
                    JsonData eventNameField = h.fields().get(EventElasticProperty.NAME.getProperty());
                    if (eventNameField != null) {
                        eventName = eventNameField.to(List.class).get(0).toString();
                    }
                    return new IdNameDTO(eventId.longValue(), eventName);
                }
        ).collect(Collectors.toList());
    }

    public SearchResponse<SessionData> getSeasonTicketSessionsSummary(SeasonTicketDTO seasonTicketDTO) {
        BoolQuery.Builder filteredQuery = SessionsESFilterDecorator.prepareSearchSessions(seasonTicketDTO);

        Map<String, Aggregation> aggregations = new HashMap<>();

        // Different events
        BoolQuery seasonTicketQuery = filteredQuery.build();
        Aggregation differentEventsAgg = new Aggregation.Builder()
                .filter(filter -> filter.bool(seasonTicketQuery))
                .aggregations(DIFFERENT_EVENTS_AGGREGATION, AggregationBuilders
                        .cardinality(c -> c.field(SessionElasticProperty.EVENT_ID.getProperty()))).build();
        aggregations.put(DIFFERENT_EVENTS_FILTER, differentEventsAgg);

        // Sessions on sale
        BoolQuery.Builder sessionsOnSaleQuery = SessionsESFilterDecorator.prepareSearchSessions(seasonTicketDTO);
        ESBuilder.addMustTerm(sessionsOnSaleQuery, SessionElasticProperty.STATUS, SessionStatus.READY.getId());
        ESBuilder.addMustTerm(sessionsOnSaleQuery, SessionElasticProperty.EVENT_STATUS, SessionStatus.READY.getId());
        Aggregation sessionsOnSaleAgg = new Aggregation.Builder()
                .filter(filter -> filter.bool(sessionsOnSaleQuery.build()))
                .aggregations(SESSIONS_ON_SALE_AGGREGATION, AggregationBuilders
                        .valueCount(c -> c.field(SessionElasticProperty.ID.getProperty()))).build();
        aggregations.put(SESSIONS_ON_SALE_FILTER, sessionsOnSaleAgg);

        // Assigned sessions
        BoolQuery.Builder assignedSessionsQuery = SessionsESFilterDecorator.prepareSearchSessions(seasonTicketDTO);
        ESBuilder.addMustTerm(assignedSessionsQuery, SessionElasticProperty.RELATED_SEASON_SESSION_IDS, seasonTicketDTO.getSessionId());
        Aggregation assignedSessionsAgg = new Aggregation.Builder()
                .filter(filter -> filter.bool(assignedSessionsQuery.build()))
                .aggregations(ASSIGNED_SESSIONS_AGGREGATION, AggregationBuilders
                        .valueCount(c -> c.field(SessionElasticProperty.ID.getProperty()))).build();
        aggregations.put(ASSIGNED_SESSIONS_FILTER, assignedSessionsAgg);

        return queryAgg(seasonTicketQuery._toQuery(), aggregations, NO_PAGE);
    }
}
