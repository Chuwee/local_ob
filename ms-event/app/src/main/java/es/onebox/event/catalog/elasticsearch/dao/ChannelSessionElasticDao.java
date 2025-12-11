package es.onebox.event.catalog.elasticsearch.dao;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
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
import es.onebox.event.catalog.elasticsearch.dto.ElasticSearchResults;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionWithParent;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.catalog.elasticsearch.properties.ChannelSessionAgencyElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.ChannelSessionElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.SessionElasticProperty;
import es.onebox.event.catalog.elasticsearch.utils.ESBuilder;
import es.onebox.event.catalog.elasticsearch.utils.ElasticSearchUtils;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ChannelSessionElasticDao extends AbstractElasticDao<ChannelSessionData> {

    public void deleteByEventAndChannel(Long eventId, Long channelId) {
        Query eventQuery = QueryBuilders.term(term -> term.field(ChannelSessionElasticProperty.EVENT_ID.getProperty()).value(eventId));
        Query channelQuery = QueryBuilders.term(term -> term.field(ChannelSessionElasticProperty.CHANNEL_ID.getProperty()).value(channelId));
        BoolQuery.Builder query = QueryBuilders.bool()
                .must(eventQuery)
                .must(channelQuery);
        delete(query);
    }

    public ElasticSearchResults<ChannelSessionData> searchChannelSessions(BoolQuery.Builder channelSessionQuery, Page page) {

        List<SortOptions> sort = Collections.singletonList(SortOptionsBuilders.field(builder -> builder.field(ChannelSessionElasticProperty.START.getProperty())));
        SearchResponse<ChannelSessionData> searchResponse = query(channelSessionQuery.build()._toQuery(), sort, page);

        Metadata metadata = new Metadata();
        metadata.setOffset((long) page.getFromItem());
        metadata.setLimit((long) page.getSize());
        metadata.setTotal(searchResponse.hits().total().value());

        List<ChannelSessionData> results = searchResponse.hits().hits().stream()
                .map(Hit::source).collect(Collectors.toList());

        ElasticSearchResults<ChannelSessionData> result = new ElasticSearchResults<>();
        result.setMetadata(metadata);
        result.setResults(results);

        return result;
    }

    public ElasticSearchResults<ChannelSessionWithParent> searchChannelSessionsWithParent(
            Long eventId, BoolQuery.Builder channelSessionQuery, Page page) {

        BoolQuery.Builder parentQuery = QueryBuilders.bool()
                .must(QueryBuilders.term(term -> term.field(SessionElasticProperty.EVENT_ID.getProperty()).value(eventId)));

        Query hasParentQuery = QueryBuilders.hasParent(builder -> builder
                .parentType(EventDataUtils.KEY_SESSION)
                .query(parentQuery.build()._toQuery())
                .innerHits(new InnerHits.Builder().build())
                .score(false));

        BoolQuery.Builder query = QueryBuilders.bool()
                .must(hasParentQuery)
                .must(channelSessionQuery.build()._toQuery());

        List<SortOptions> sort = Collections.singletonList(SortOptionsBuilders.field(builder ->
                builder.field(ChannelSessionElasticProperty.START.getProperty())));
        SearchResponse<ChannelSessionData> searchResponse = query(query.build()._toQuery(), sort, page);

        Metadata metadata = new Metadata();
        metadata.setOffset((long) page.getFromItem());
        metadata.setLimit((long) page.getSize());
        metadata.setTotal(searchResponse.hits().total().value());

        List<ChannelSessionWithParent> results = new ArrayList<>();
        for (Hit<ChannelSessionData> hit : searchResponse.hits().hits()) {
            ChannelSessionWithParent result = new ChannelSessionWithParent();
            result.setChannelSession(hit.source().getChannelSession());
            InnerHitsResult innerHits = hit.innerHits().getOrDefault(EventDataUtils.KEY_SESSION, null);
            for (Hit<JsonData> innerHit : innerHits.hits().hits()) {
                SessionData sessionData = innerHit.source().to(SessionData.class);
                result.setSessionData(sessionData);
            }
            results.add(result);
        }

        return new ElasticSearchResults<>(metadata, results);
    }

    public List<ChannelSessionData> getByEventId(Long eventId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.EVENT_ID, eventId);
        return ElasticSearchUtils.getAll(this, queryBuilder, EventDataUtils.getEventKey(eventId));
    }

    public List<ChannelSessionData> getBySessionId(Long sessionId, Long eventId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.SESSION_ID, sessionId);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.EVENT_ID, eventId);
        return ElasticSearchUtils.getAll(this, queryBuilder, EventDataUtils.getEventKey(eventId));
    }

    public List<ChannelSessionData> getOccupationFieldsByEventAndChannelId(Long eventId, Long channelId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.EVENT_ID, eventId);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.CHANNEL_ID, channelId);
        return ElasticSearchUtils.getAllFilteredFields(this, queryBuilder, EventDataUtils.getEventKey(eventId),
                List.of(ChannelSessionElasticProperty.SESSION_ID.getProperty(),
                        ChannelSessionElasticProperty.SOLD_OUT.getProperty(),
                        ChannelSessionElasticProperty.PRICES_MIN_BASE.getProperty(),
                        ChannelSessionElasticProperty.PRICES_MAX_BASE.getProperty(),
                        ChannelSessionElasticProperty.PRICES_MIN_BASE_PROMOTED.getProperty(),
                        ChannelSessionElasticProperty.PRICES_MIN_NET.getProperty(),
                        ChannelSessionElasticProperty.PRICES_MAX_NET.getProperty(),
                        ChannelSessionElasticProperty.PRICES_MIN_NET_PROMOTED.getProperty(),
                        ChannelSessionElasticProperty.PRICES_MIN_FINAL.getProperty(),
                        ChannelSessionElasticProperty.PRICES_MAX_FINAL.getProperty(),
                        ChannelSessionElasticProperty.PRICES_MIN_FINAL_PROMOTED.getProperty())
        );
    }

    public List<ChannelSessionData> getBySessionAndChannelId(List<Long> sessionIds, Long channelId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerms(queryBuilder, ChannelSessionElasticProperty.SESSION_ID, sessionIds);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.CHANNEL_ID, channelId);
        return ElasticSearchUtils.getAll(this, queryBuilder, null);
    }

}
