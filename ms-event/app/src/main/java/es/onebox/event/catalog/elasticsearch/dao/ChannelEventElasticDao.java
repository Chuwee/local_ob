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
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventWithParent;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.properties.ChannelEventElasticProperty;
import es.onebox.event.catalog.elasticsearch.utils.ESBuilder;
import es.onebox.event.catalog.elasticsearch.utils.ElasticSearchUtils;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ChannelEventElasticDao extends AbstractElasticDao<ChannelEventData> {

    public ChannelEventData get(Long channelId, Long eventId) {
        return findByID(EventDataUtils.getChannelEventKey(channelId, eventId), EventDataUtils.getEventKey(eventId));
    }

    public void delete(Long channelId, Long eventId) {
        deleteById(EventDataUtils.getChannelEventKey(channelId, eventId), EventDataUtils.getEventKey(eventId));
    }

    public ElasticSearchResults<ChannelEventWithParent> searchChannelEventsWithParent(
            BoolQuery.Builder channelEventQuery, BoolQuery.Builder eventQuery, BoolQuery.Builder channelSessionQuery,
            Page page, String[] fields, ChannelCatalogEventsFilter filter) {

        BoolQuery.Builder parentQuery = QueryBuilders.bool()
                .must(QueryBuilders.exists(exists -> exists.field("join")))
                .must(eventQuery.build()._toQuery());

        if (channelSessionQuery != null) {
            Query hasChildSessionChannelQuery = QueryBuilders.hasChild(builder -> builder
                    .type(EventDataUtils.KEY_CHANNEL_SESSION)
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
                builder.field(ChannelEventElasticProperty.CATALOG_START_DATE.getProperty())));
        SearchResponse<ChannelEventData> searchResponse = query(query.build()._toQuery(), sort, fields, page);

        Metadata metadata = new Metadata();
        metadata.setOffset((long) page.getFromItem());
        metadata.setLimit((long) page.getSize());
        metadata.setTotal(searchResponse.hits().total().value());

        List<ChannelEventWithParent> results = new ArrayList<>();
        for (Hit<ChannelEventData> hit : searchResponse.hits().hits()) {
            ChannelEventWithParent result = new ChannelEventWithParent();
            result.setChannelEvent(hit.source().getChannelEvent());
            InnerHitsResult innerHits = hit.innerHits().getOrDefault(EventDataUtils.KEY_EVENT, null);
            for (Hit<JsonData> innerHit : innerHits.hits().hits()) {
                EventData eventData = innerHit.source().to(EventData.class);
                result.setEventData(eventData);
            }
            results.add(result);
        }

        return new ElasticSearchResults<>(metadata, results);
    }

    public List<ChannelEventData> getByEventId(Long eventId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelEventElasticProperty.EVENT_ID, eventId);
        return ElasticSearchUtils.getAll(this, queryBuilder, EventDataUtils.getEventKey(eventId));
    }

    public List<ChannelEventData> getByChannelId(Long channelId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelEventElasticProperty.CHANNEL_ID, channelId);
        return ElasticSearchUtils.getAll(this, queryBuilder, null);
    }

}
