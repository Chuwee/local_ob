package es.onebox.event.seasontickets.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.catalog.elasticsearch.properties.EventElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.SessionElasticProperty;
import es.onebox.event.catalog.elasticsearch.utils.ESBuilder;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SessionAssignationStatusDTO;
import es.onebox.event.seasontickets.request.SeasonTicketSessionsEventsFilter;
import es.onebox.event.seasontickets.request.SeasonTicketSessionsSearchFilter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class SessionsESFilterDecorator {

    private SessionsESFilterDecorator() {
        throw new UnsupportedOperationException("Cannot instantiate class");
    }

    private static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static BoolQuery.Builder prepareSearchSessions(SeasonTicketDTO seasonTicketDTO) {
        BoolQuery.Builder querySession = QueryBuilders.bool();
        ESBuilder.addMustTerm(querySession, SessionElasticProperty.ENTITY_ID, seasonTicketDTO.getEntityId());
        ESBuilder.addMustTerm(querySession, SessionElasticProperty.VENUE_ID, seasonTicketDTO.getVenues().get(0).getId());
        ESBuilder.addMustTerm(querySession, SessionElasticProperty.IS_SEASON_PACK_SESSION, false);
        ESBuilder.addMustTerm(querySession, SessionElasticProperty.EVENT_TYPE, EventType.NORMAL.getId());
        ESBuilder.addMustTerms(querySession, SessionElasticProperty.EVENT_STATUS, getFilteredEventStatus());
        return querySession;
    }

    public static BoolQuery.Builder prepareSearchSessionEvents(SeasonTicketDTO seasonTicketDTO, SeasonTicketSessionsEventsFilter filter) {
        BoolQuery.Builder query = QueryBuilders.bool();

        ESBuilder.addMustTerm(query, EventElasticProperty.ENTITY_ID, seasonTicketDTO.getEntityId());
        ESBuilder.addMustTerm(query, EventElasticProperty.TYPE, EventType.NORMAL.getId());
        query.must(QueryBuilders.nested(nested -> nested
                .path(EventElasticProperty.VENUE.getProperty())
                .query(QueryBuilders.match(match -> match.field(EventElasticProperty.VENUE_ID.getProperty()).query(seasonTicketDTO.getVenues().get(0).getId())))
                .scoreMode(ChildScoreMode.None)));

        if (filter.getFreeSearch() != null) {
            query.should(QueryBuilders.match(match -> match.field(EventElasticProperty.NAME_FPS.getProperty()).query(filter.getFreeSearch())));
            if (isNumeric(filter.getFreeSearch())) {
                query.should(QueryBuilders.match(match -> match.field(EventElasticProperty.ID.getProperty()).query(filter.getFreeSearch())));
            }
            query.minimumShouldMatch("1");
        }

        return query;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public static BoolQuery.Builder prepareSearchSessionsWithFilters(SeasonTicketSessionsSearchFilter filter, SeasonTicketDTO seasonTicketDTO) {

        BoolQuery.Builder querySession = prepareSearchSessions(seasonTicketDTO);

        if (filter.getEventId() != null) {
            ESBuilder.addMustTerm(querySession, SessionElasticProperty.EVENT_ID, filter.getEventId());
        }

        if (filter.getSessionId() != null) {
            ESBuilder.addMustTerm(querySession, SessionElasticProperty.ID, filter.getSessionId());
        }

        if (!CommonUtils.isEmpty(filter.getStartDate())) {
            Query rangeQueryBuilder = QueryBuilders.range(range -> {
                RangeQuery.Builder field = range.field(SessionElasticProperty.BEGIN_DATE.getProperty());
                for (FilterWithOperator<ZonedDateTime> startDateFilter : filter.getStartDate()) {
                    switch (startDateFilter.getOperator()) {
                        case GREATER_THAN_OR_EQUALS:
                            field.gte(JsonData.of(getTimestampFromDate(startDateFilter)));
                            break;
                        case LESS_THAN_OR_EQUALS:
                            field.lte(JsonData.of(getTimestampFromDate(startDateFilter)));
                            break;
                        default:
                            break;
                    }
                }
                return field;
            });
            querySession.must(rangeQueryBuilder);
        }

        if (filter.getFreeSearch() != null) {
            querySession.should(QueryBuilders.match(match -> match.field(SessionElasticProperty.NAME_FTS.getProperty()).query(filter.getFreeSearch())));
            querySession.should(QueryBuilders.match(match -> match.field(SessionElasticProperty.EVENT_NAME_FTS.getProperty()).query(filter.getFreeSearch())));
            querySession.minimumShouldMatch("1");
        }

        if (filter.getAssignationStatus() != null) {
            if (SessionAssignationStatusDTO.ASSIGNED.equals(filter.getAssignationStatus())) {
                ESBuilder.addMustTerm(querySession, SessionElasticProperty.RELATED_SEASON_SESSION_IDS, seasonTicketDTO.getSessionId());
            } else {
                ESBuilder.addMustNotTerms(querySession, SessionElasticProperty.RELATED_SEASON_SESSION_IDS, Collections.singletonList(seasonTicketDTO.getSessionId()));
            }
        }

        return querySession;
    }

    private static List<Integer> getFilteredEventStatus() {
        return Arrays.asList(EventStatus.PLANNED.getId(),
                EventStatus.IN_PROGRAMMING.getId(),
                EventStatus.READY.getId(),
                EventStatus.FINISHED.getId());
    }

    private static Long getTimestampFromDate(FilterWithOperator<ZonedDateTime> dateFilter) {
        Instant date = dateFilter.getValue().toInstant();
        return Timestamp.from(date).getTime();
    }
}
