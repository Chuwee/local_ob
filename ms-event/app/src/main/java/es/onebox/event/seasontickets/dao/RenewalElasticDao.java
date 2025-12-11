package es.onebox.event.seasontickets.dao;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.aggregations.CompositeAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.CompositeAggregationSource;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import es.onebox.elasticsearch.dao.AbstractElasticDao;
import es.onebox.elasticsearch.dao.Page;
import es.onebox.event.seasontickets.dao.dto.RenewalDataElastic;
import es.onebox.event.seasontickets.dto.renewals.RenewalSeatsPurgeFilter;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeat;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsFilter;
import es.onebox.event.seasontickets.elasticsearch.PaginationUtils;
import es.onebox.event.seasontickets.elasticsearch.RenewalsESUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.onebox.event.seasontickets.elasticsearch.RenewalsESUtils.AGGREGATION_DATA_ENTITY_ID;
import static es.onebox.event.seasontickets.elasticsearch.RenewalsESUtils.AGGREGATION_DATA_ENTITY_NAME;

@Repository
public class RenewalElasticDao extends AbstractElasticDao<RenewalDataElastic> {

    private static final Page NO_PAGE = new Page(0, 0);

    public SearchResponse<RenewalDataElastic> getRenewalSeats(SeasonTicketRenewalSeatsFilter filter) {
        BoolQuery.Builder query = RenewalsESUtils.prepareFilterGetRenewalSeats(filter);
        Page page = PaginationUtils.buildPage(filter);
        List<SortOptions> sort = RenewalsESUtils.prepareSortGetRenewalSeats(filter.getSort());

        return query(query.build()._toQuery(), sort, page);
    }

    public List<SeasonTicketRenewalSeat> convertSearchResponseIntoRenewalSeat(SearchResponse<RenewalDataElastic> resultSet) {
        return resultSet.hits().hits().stream().map(h -> elasticSearchObjectMapper.convertValue(
                h.source(), SeasonTicketRenewalSeat.class)).collect(Collectors.toList());
    }

    public SearchResponse<RenewalDataElastic> getRenewalMappedSeats(Long seasonTicketId) {
        BoolQuery.Builder query = RenewalsESUtils.prepareMappedRenewalSeats(seasonTicketId);
        return count(query.build()._toQuery());
    }

    public SearchResponse<RenewalDataElastic> getRenewalNotMappedSeats(Long seasonTicketId) {
        BoolQuery.Builder query = RenewalsESUtils.prepareNotMappedRenewalSeats(seasonTicketId);
        return count(query.build()._toQuery());
    }

    public SearchResponse<RenewalDataElastic> getRenewalSeatsPurge(Long seasonTicketId, RenewalSeatsPurgeFilter filter, Page page) {
        BoolQuery.Builder query = RenewalsESUtils.prepareRenewalSeatsPurge(seasonTicketId, filter);
        List<SortOptions> sort = RenewalsESUtils.prepareSortGetRenewalSeats(null);
        return query(query.build()._toQuery(), sort, page);
    }

    public SearchResponse<RenewalDataElastic> getRenewalSeatsPurgeCount(Long seasonTicketId, RenewalSeatsPurgeFilter filter) {
        BoolQuery.Builder query = RenewalsESUtils.prepareRenewalSeatsPurge(seasonTicketId, filter);
        return count(query.build()._toQuery());
    }

    public SearchResponse<RenewalDataElastic> getRenewalSeatsTotalCount(Long seasonTicketId) {
        SeasonTicketRenewalSeatsFilter filter = new SeasonTicketRenewalSeatsFilter();
        filter.setSeasonTicketId(seasonTicketId);
        BoolQuery.Builder query = RenewalsESUtils.prepareFilterGetRenewalSeats(filter);
        return count(query.build()._toQuery());
    }

    public SearchResponse<RenewalDataElastic> getRenewalSeatsCountAfterPurge(Long seasonTicketId, RenewalSeatsPurgeFilter renewalSeatsPurgeFilter) {
        BoolQuery.Builder query = RenewalsESUtils.prepareFilterGetRenewalSeatsAfterPurge(seasonTicketId, renewalSeatsPurgeFilter);
        return count(query.build()._toQuery());
    }

    public List<RenewalDataElastic> convertSearchResponseIntoRenewalDataElastic(SearchResponse<RenewalDataElastic> searchResponse) {
        return searchResponse.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
    }

    public Long getTotalHits(SearchResponse<RenewalDataElastic> searchResponse) {
        return searchResponse.hits().total().value();
    }

    public SearchResponse<RenewalDataElastic> getRenewalEntities(SeasonTicketRenewalSeatsFilter filter) {
        BoolQuery.Builder query = RenewalsESUtils.prepareFilterGetRenewalSeats(filter);

        CompositeAggregation compositeAggregation = CompositeAggregation.of(c -> c
                .size(100)
                .sources(
                        Map.of(RenewalsESUtils.AGGREGATION_DATA_COMPOSITE_SOURCE_NAME,
                                CompositeAggregationSource.of(agg -> agg.terms(t -> t.field(AGGREGATION_DATA_ENTITY_NAME)))),
                        Map.of(RenewalsESUtils.AGGREGATION_DATA_COMPOSITE_SOURCE_ID,
                                CompositeAggregationSource.of(agg -> agg.terms(t -> t.field(AGGREGATION_DATA_ENTITY_ID))))
                ));

        return queryAgg(query.build()._toQuery(), RenewalsESUtils.AGGREGATION_DATA,
                compositeAggregation._toAggregation(), NO_PAGE);
    }
}
