package es.onebox.event.catalog.elasticsearch.dao;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.InnerHits;
import co.elastic.clients.elasticsearch.core.search.InnerHitsResult;
import co.elastic.clients.json.JsonData;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.elasticsearch.dao.AbstractElasticDao;
import es.onebox.elasticsearch.dao.Page;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventsFilter;
import es.onebox.event.catalog.elasticsearch.dto.ElasticSearchResults;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgencyWithParent;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.properties.ChannelEventAgencyElasticProperty;
import es.onebox.event.catalog.elasticsearch.utils.ESBuilder;
import es.onebox.event.catalog.elasticsearch.utils.ElasticSearchUtils;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ChannelEventAgencyElasticDao extends AbstractElasticDao<ChannelEventAgencyData> {

    public ChannelEventAgencyData get(Long channelId, Long eventId, Long agencyId) {
        return findByID(EventDataUtils.getChannelEventAgencyKey(channelId, eventId, agencyId), EventDataUtils.getEventKey(eventId));
    }

    public void delete(Long channelId, Long eventId, Long agencyId) {
        deleteById(EventDataUtils.getChannelEventAgencyKey(channelId, eventId, agencyId), EventDataUtils.getEventKey(eventId));
    }

    public ElasticSearchResults<ChannelEventAgencyWithParent> searchChannelEventsWithParent(
            BoolQuery.Builder channelEventQuery, BoolQuery.Builder eventQuery, BoolQuery.Builder channelSessionQuery,
            Page page, String[] fields, ChannelCatalogEventsFilter filter) {

        BoolQuery.Builder parentQuery = QueryBuilders.bool()
                .must(QueryBuilders.exists(exists -> exists.field("join")))
                .must(eventQuery.build()._toQuery());

        if (channelSessionQuery != null) {
            Query hasChildSessionChannelQuery = QueryBuilders.hasChild(builder -> builder
                    .type(EventDataUtils.KEY_CHANNEL_SESSION_AGENCY)
                    .query(channelSessionQuery.build()._toQuery())
                    .scoreMode(ChildScoreMode.None));

            Query hasChildSessionQuery = QueryBuilders.hasChild(builder -> builder
                    .type(EventDataUtils.KEY_SESSION)
                    .query(hasChildSessionChannelQuery)
                    .scoreMode(ChildScoreMode.None));

            parentQuery.must(hasChildSessionQuery);
        }

        Query hasParentQuery = QueryBuilders.hasParent(builder -> builder
                .parentType(EventDataUtils.KEY_EVENT)
                .query(parentQuery.build()._toQuery())
                .innerHits(new InnerHits.Builder().build())
                .score(false));

        BoolQuery.Builder query = QueryBuilders.bool()
                .must(hasParentQuery)
                .must(channelEventQuery.build()._toQuery());

        List<SortOptions> sort = ESBuilder.prepareSort(filter.getSort());
        sort.add(SortOptionsBuilders.field(builder ->
                builder.field(ChannelEventAgencyElasticProperty.CATALOG_START_DATE.getProperty())));
        SearchResponse<ChannelEventAgencyData> searchResponse = query(query.build()._toQuery(), sort, fields, page);

        Metadata metadata = new Metadata();
        metadata.setOffset((long) page.getFromItem());
        metadata.setLimit((long) page.getSize());
        metadata.setTotal(searchResponse.hits().total().value());

        List<ChannelEventAgencyWithParent> results = new ArrayList<>();
        for (Hit<ChannelEventAgencyData> hit : searchResponse.hits().hits()) {
            ChannelEventAgencyWithParent result = new ChannelEventAgencyWithParent();
            result.setChannelEventAgency(hit.source().getChannelEventAgency());
            InnerHitsResult innerHits = hit.innerHits().getOrDefault(EventDataUtils.KEY_EVENT, null);
            for (Hit<JsonData> innerHit : innerHits.hits().hits()) {
                EventData eventData = innerHit.source().to(EventData.class);
                result.setEventData(eventData);
            }
            results.add(result);
        }

        return new ElasticSearchResults<>(metadata, results);
    }

    public List<ChannelEventAgencyData> getByEventId(Long eventId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelEventAgencyElasticProperty.EVENT_ID, eventId);
        return ElasticSearchUtils.getAll(this, queryBuilder, EventDataUtils.getEventKey(eventId));
    }

    public List<ChannelEventAgencyData> getByEventIdAndAgencyId(Long eventId, Long agencyId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelEventAgencyElasticProperty.EVENT_ID, eventId);
        ESBuilder.addMustTerm(queryBuilder, ChannelEventAgencyElasticProperty.AGENCY_ID, agencyId);
        return ElasticSearchUtils.getAll(this, queryBuilder, EventDataUtils.getEventKey(eventId));
    }

    public List<ChannelEventAgencyData> getByChannelId(Long channelId, Long agencyId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelEventAgencyElasticProperty.CHANNEL_ID, channelId);
        ESBuilder.addMustTerm(queryBuilder, ChannelEventAgencyElasticProperty.AGENCY_ID, agencyId);
        return ElasticSearchUtils.getAll(this, queryBuilder, null);
    }

}
