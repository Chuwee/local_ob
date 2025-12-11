package es.onebox.event.seasontickets.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.CompositeBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortDirection;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.catalog.elasticsearch.utils.ESBuilder;
import es.onebox.event.seasontickets.converter.SeasonTicketRenewalsFilterConverter;
import es.onebox.event.seasontickets.dao.dto.MappingStatusES;
import es.onebox.event.seasontickets.dao.dto.RenewalDataElastic;
import es.onebox.event.seasontickets.dto.renewals.RenewalEntitiesResponse;
import es.onebox.event.seasontickets.dto.renewals.RenewalEntityDTO;
import es.onebox.event.seasontickets.dto.renewals.RenewalSeatsPurgeFilter;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsFilter;
import es.onebox.event.seasontickets.dto.renewals.SeatRenewalStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RenewalsESUtils {

    public static final String FAKE = "FAKE";
    public static final String AGGREGATION_DATA_COMPOSITE_SOURCE_ID = "id";
    public static final String AGGREGATION_DATA_COMPOSITE_SOURCE_NAME = "name";
    public static final String AGGREGATION_DATA = "data";
    public static final String AGGREGATION_DATA_ENTITY_ID = "entityId";
    public static final String AGGREGATION_DATA_ENTITY_NAME = "entityName";

    private RenewalsESUtils() {
        throw new UnsupportedOperationException("Cannot instantiate class");
    }

    public static BoolQuery.Builder prepareFilterGetRenewalSeats(SeasonTicketRenewalSeatsFilter filter) {
        BoolQuery.Builder query = QueryBuilders.bool();

        if (filter.getSeasonTicketId() != null) {
            ESBuilder.addMustTerm(query, RenewalESProperty.SEASON_TICKET_ID, filter.getSeasonTicketId());
        }

        if (filter.getEntityId() != null) {
            ESBuilder.addMustTerm(query, RenewalESProperty.ENTITY_ID, filter.getEntityId());
        }

        if (CollectionUtils.isNotEmpty(filter.getUserIds())) {
            ESBuilder.addMustTerms(query, RenewalESProperty.USER_ID, filter.getUserIds());
        }

        if (CollectionUtils.isNotEmpty(filter.getRenewalIds())) {
            ESBuilder.addIds(query, filter.getRenewalIds());
        }

        if (filter.getMappingStatus() != null) {
            ESBuilder.addMustMatch(query, RenewalESProperty.MAPPING_STATUS, filter.getMappingStatus().name());
        }

        if (filter.getFreeSearch() != null) {
            ESBuilder.addShouldMatch(query, RenewalESProperty.NAME_FTS, filter.getFreeSearch());
            ESBuilder.addShouldMatch(query, RenewalESProperty.SURNAME_FTS, filter.getFreeSearch());
            ESBuilder.addShouldMatch(query, RenewalESProperty.MEMBER_ID_FTS, filter.getFreeSearch());
            ESBuilder.addShouldMatch(query, RenewalESProperty.EMAIL_FTS, filter.getFreeSearch());
            ESBuilder.addShouldMatch(query, RenewalESProperty.SEASONT_TICKET_NAME_FTS, filter.getFreeSearch());
            ESBuilder.addShouldMatch(query, RenewalESProperty.MANAGER_FTS, filter.getFreeSearch());
            query.minimumShouldMatch("1");
        }

        if (filter.getActualRateId() != null) {
            ESBuilder.addMustTerm(query, RenewalESProperty.ACTUAL_RATE_ID, filter.getActualRateId());
        }

        if (!CommonUtils.isEmpty(filter.getBirthday())) {
            query.must(birthdayRangeQuery(filter.getBirthday()));
        }

        if (filter.getAutoRenewal() != null) {
            ESBuilder.addMustTerm(query, RenewalESProperty.AUTO_RENEWAL, filter.getAutoRenewal());
        }

        if (filter.getRenewalSubstatus() != null) {
            ESBuilder.addMustMatch(query, RenewalESProperty.RENEWAL_SUBSTATUS, filter.getRenewalSubstatus());
        } else if (filter.getRenewalStatus() != null) {
            ESBuilder.addMustMatch(query, RenewalESProperty.RENEWAL_STATUS, filter.getRenewalStatus().name());
            if (BooleanUtils.isTrue(filter.getStrictStatus())) {
                ESBuilder.addMustNotExists(query, RenewalESProperty.RENEWAL_SUBSTATUS);
            }
        }

        return query;
    }

    public static List<SortOptions> prepareSortGetRenewalSeats(SortOperator<RenewalsSortableField> sortOperator) {
        List<SortOptions> sortCriteria = new ArrayList<>();

        if (sortOperator != null && CollectionUtils.isNotEmpty(sortOperator.getSortDirections())) {
            for (SortDirection<RenewalsSortableField> direction : sortOperator.getSortDirections()) {
                sortCriteria.add(es.onebox.elasticsearch.utils.SortBuilder.addFieldSorting(direction));
            }
        }

        // Add always a sort by ID in last case
        sortCriteria.add(SortOptionsBuilders.field(builder -> builder
                .field(RenewalESProperty.SEASON_TICKET_ID.getProperty()).order(SortOrder.Desc)));

        return sortCriteria;
    }

    public static BoolQuery.Builder prepareMappedRenewalSeats(Long seasonTicketId) {
        SeasonTicketRenewalSeatsFilter seasonTicketRenewalSeatsFilter = new SeasonTicketRenewalSeatsFilter();
        seasonTicketRenewalSeatsFilter.setSeasonTicketId(seasonTicketId);
        BoolQuery.Builder query = prepareFilterGetRenewalSeats(seasonTicketRenewalSeatsFilter);
        ESBuilder.addMustMatch(query, RenewalESProperty.MAPPING_STATUS, MappingStatusES.MAPPED.name());
        return query;
    }

    public static BoolQuery.Builder prepareNotMappedRenewalSeats(Long seasonTicketId) {
        SeasonTicketRenewalSeatsFilter seasonTicketRenewalSeatsFilter = new SeasonTicketRenewalSeatsFilter();
        seasonTicketRenewalSeatsFilter.setSeasonTicketId(seasonTicketId);
        BoolQuery.Builder query = prepareFilterGetRenewalSeats(seasonTicketRenewalSeatsFilter);
        ESBuilder.addMustMatch(query, RenewalESProperty.MAPPING_STATUS, MappingStatusES.NOT_MAPPED.name());
        return query;
    }

    public static BoolQuery.Builder prepareRenewalSeatsPurge(Long seasonTicketId, RenewalSeatsPurgeFilter purgeFilter) {
        SeasonTicketRenewalSeatsFilter filter =
                SeasonTicketRenewalsFilterConverter.convertToSeasonTicketRenewalSeatsFilter(seasonTicketId, purgeFilter);
        BoolQuery.Builder query = prepareFilterGetRenewalSeats(filter);
        query.mustNot(QueryBuilders.match(match -> match
                .field(RenewalESProperty.RENEWAL_STATUS.getProperty()).query(SeatRenewalStatus.RENEWED.name())));
        return query;
    }

    public static BoolQuery.Builder prepareFilterGetRenewalSeatsAfterPurge(Long seasonTicketId, RenewalSeatsPurgeFilter renewalSeatsPurgeFilter) {
        BoolQuery.Builder query = QueryBuilders.bool();
        query.minimumShouldMatch("1");
        ESBuilder.addMustTerm(query, RenewalESProperty.SEASON_TICKET_ID, seasonTicketId.intValue());

        if (renewalSeatsPurgeFilter.getMappingStatus() != null) {
            addMustNotToShouldQuery(query, RenewalESProperty.MAPPING_STATUS.getProperty(), renewalSeatsPurgeFilter.getMappingStatus().name());
        }

        if (renewalSeatsPurgeFilter.getRenewalStatus() != null && !SeatRenewalStatus.RENEWED.equals(renewalSeatsPurgeFilter.getRenewalStatus())) {
            addMustNotToShouldQuery(query, RenewalESProperty.RENEWAL_STATUS.getProperty(), renewalSeatsPurgeFilter.getRenewalStatus().name());
        } else if (renewalSeatsPurgeFilter.getRenewalStatus() != null && SeatRenewalStatus.RENEWED.equals(renewalSeatsPurgeFilter.getRenewalStatus())) {
            addMustNotToShouldQuery(query, RenewalESProperty.RENEWAL_STATUS.getProperty(), FAKE);
        } else {
            ESBuilder.addShouldMatch(query, RenewalESProperty.RENEWAL_STATUS, SeatRenewalStatus.RENEWED.name());
        }

        if (renewalSeatsPurgeFilter.getFreeSearch() != null) {
            String freeSearch = renewalSeatsPurgeFilter.getFreeSearch();


            BoolQuery.Builder freeSearchSubQuery = QueryBuilders.bool();
            ESBuilder.addShouldMatch(freeSearchSubQuery, RenewalESProperty.NAME_FTS, freeSearch);
            ESBuilder.addShouldMatch(freeSearchSubQuery, RenewalESProperty.SURNAME_FTS, freeSearch);
            ESBuilder.addShouldMatch(freeSearchSubQuery, RenewalESProperty.MEMBER_ID_FTS, freeSearch);
            ESBuilder.addShouldMatch(freeSearchSubQuery, RenewalESProperty.EMAIL_FTS, freeSearch);
            ESBuilder.addShouldMatch(freeSearchSubQuery, RenewalESProperty.ADDRESS_FTS, freeSearch);
            ESBuilder.addShouldMatch(freeSearchSubQuery, RenewalESProperty.POSTAL_CODE_FTS, freeSearch);
            ESBuilder.addShouldMatch(freeSearchSubQuery, RenewalESProperty.PHONE_NUMBER_FTS, freeSearch);
            ESBuilder.addShouldMatch(freeSearchSubQuery, RenewalESProperty.SEASONT_TICKET_NAME_FTS, freeSearch);
            ESBuilder.addShouldMatch(freeSearchSubQuery, RenewalESProperty.MANAGER_FTS, freeSearch);
            freeSearchSubQuery.minimumShouldMatch("1");

            addMustNotSubQueryToShouldQuery(query, freeSearchSubQuery.build()._toQuery());
        }

        if (!CommonUtils.isEmpty(renewalSeatsPurgeFilter.getBirthday())) {
            addMustNotSubQueryToShouldQuery(query, birthdayRangeQuery(renewalSeatsPurgeFilter.getBirthday()));
        }

        return query;
    }


    public static RenewalEntitiesResponse generateRenewalEntitiesResponse(SearchResponse<RenewalDataElastic> searchResponse) {
        RenewalEntitiesResponse response = new RenewalEntitiesResponse();
        if (searchResponse.aggregations().containsKey(AGGREGATION_DATA)) {
            List<CompositeBucket> aggs = searchResponse.aggregations().get(AGGREGATION_DATA).composite().buckets().array();
            List<RenewalEntityDTO> renewalEntities = new ArrayList<>();
            for (CompositeBucket agg : aggs) {
                Map<String, FieldValue> key = agg.key();
                String name = key.get(AGGREGATION_DATA_COMPOSITE_SOURCE_NAME) != null ?
                        key.get(AGGREGATION_DATA_COMPOSITE_SOURCE_NAME).stringValue() : null;
                Long id = key.get(AGGREGATION_DATA_COMPOSITE_SOURCE_ID) != null ?
                        key.get(AGGREGATION_DATA_COMPOSITE_SOURCE_ID).longValue() : null;
                if (id != null && name != null) {
                    renewalEntities.add(new RenewalEntityDTO(id, name));
                }
            }
            response.setRenewalEntities(renewalEntities);
        }
        return response;
    }

    private static Query birthdayRangeQuery(List<FilterWithOperator<ZonedDateTime>> birthday) {
        return QueryBuilders.range(range -> {
            RangeQuery.Builder field = range.field(RenewalESProperty.BIRTHDAY.getProperty());
            for (FilterWithOperator<ZonedDateTime> startDateFilter : birthday) {
                switch (startDateFilter.getOperator()) {
                    case GREATER_THAN_OR_EQUALS:
                        field.gte(JsonData.of(startDateFilter.getValue().toLocalDate()));
                        break;
                    case LESS_THAN_OR_EQUALS:
                        field.lte(JsonData.of(startDateFilter.getValue().toLocalDate()));
                        break;
                    default:
                        break;
                }
            }
            return field;
        });
    }

    private static void addMustNotToShouldQuery(BoolQuery.Builder rootQuery, String field, String value) {
        BoolQuery.Builder query = QueryBuilders.bool();
        query.mustNot(QueryBuilders.match(match -> match.field(field).query(value)));
        rootQuery.should(query.build()._toQuery());
    }

    private static void addMustNotSubQueryToShouldQuery(BoolQuery.Builder rootQuery, Query subQuery) {
        BoolQuery.Builder queryWrapper = QueryBuilders.bool();
        queryWrapper.mustNot(subQuery);
        rootQuery.should(queryWrapper.build()._toQuery());
    }

}
