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
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyWithParent;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.catalog.elasticsearch.properties.ChannelSessionAgencyElasticProperty;
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
public class ChannelSessionAgencyElasticDao extends AbstractElasticDao<ChannelSessionAgencyData> {

    public void deleteByEventAndChannel(Long eventId, Long channelId) {
        Query eventQuery = QueryBuilders.term(term -> term.field(ChannelSessionAgencyElasticProperty.EVENT_ID.getProperty()).value(eventId));
        Query channelQuery = QueryBuilders.term(term -> term.field(ChannelSessionAgencyElasticProperty.CHANNEL_ID.getProperty()).value(channelId));
        BoolQuery.Builder query = QueryBuilders.bool()
                .must(eventQuery)
                .must(channelQuery);
        delete(query);
    }

    public ElasticSearchResults<ChannelSessionAgencyData> searchChannelSessions(BoolQuery.Builder channelSessionQuery, Page page) {

        List<SortOptions> sort = Collections.singletonList(SortOptionsBuilders.field(builder -> builder.field(ChannelSessionAgencyElasticProperty.START.getProperty())));
        SearchResponse<ChannelSessionAgencyData> searchResponse = query(channelSessionQuery.build()._toQuery(), sort, page);

        Metadata metadata = new Metadata();
        metadata.setOffset((long) page.getFromItem());
        metadata.setLimit((long) page.getSize());
        metadata.setTotal(searchResponse.hits().total().value());

        List<ChannelSessionAgencyData> results = searchResponse.hits().hits().stream()
                .map(Hit::source).collect(Collectors.toList());

        ElasticSearchResults<ChannelSessionAgencyData> result = new ElasticSearchResults<>();
        result.setMetadata(metadata);
        result.setResults(results);

        return result;
    }

    public ElasticSearchResults<ChannelSessionAgencyWithParent> searchChannelSessionsWithParent(
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
                builder.field(ChannelSessionAgencyElasticProperty.START.getProperty())));
        SearchResponse<ChannelSessionAgencyData> searchResponse = query(query.build()._toQuery(), sort, page);

        Metadata metadata = new Metadata();
        metadata.setOffset((long) page.getFromItem());
        metadata.setLimit((long) page.getSize());
        metadata.setTotal(searchResponse.hits().total().value());

        List<ChannelSessionAgencyWithParent> results = new ArrayList<>();
        for (Hit<ChannelSessionAgencyData> hit : searchResponse.hits().hits()) {
            ChannelSessionAgencyWithParent result = new ChannelSessionAgencyWithParent();
            result.setChannelSessionAgency(hit.source().getChannelSessionAgency());
            InnerHitsResult innerHits = hit.innerHits().getOrDefault(EventDataUtils.KEY_SESSION, null);
            for (Hit<JsonData> innerHit : innerHits.hits().hits()) {
                SessionData sessionData = innerHit.source().to(SessionData.class);
                result.setSessionData(sessionData);
            }
            results.add(result);
        }

        return new ElasticSearchResults<>(metadata, results);
    }

    public List<ChannelSessionAgencyData> getByEventId(Long eventId, Long agencyId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.EVENT_ID, eventId);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.AGENCY_ID, agencyId);
        return ElasticSearchUtils.getAll(this, queryBuilder, EventDataUtils.getEventKey(eventId));
    }

    public List<ChannelSessionAgencyData> getByEventId(Long eventId, List<Long> agencyIds) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.EVENT_ID, eventId);
        ESBuilder.addMustTerms(queryBuilder, ChannelSessionAgencyElasticProperty.AGENCY_ID, agencyIds);
        return ElasticSearchUtils.getAll(this, queryBuilder, EventDataUtils.getEventKey(eventId));
    }

    public List<ChannelSessionAgencyData> getBySessionId(Long sessionId, Long eventId, List<Long> agencyIds) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.SESSION_ID, sessionId);
        ESBuilder.addMustTerms(queryBuilder, ChannelSessionAgencyElasticProperty.AGENCY_ID, agencyIds);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.EVENT_ID, eventId);
        return ElasticSearchUtils.getAll(this, queryBuilder, EventDataUtils.getEventKey(eventId));
    }

    public List<ChannelSessionAgencyData> getBySessionId(Long sessionId, Long eventId, Long agencyId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.SESSION_ID, sessionId);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.AGENCY_ID, agencyId);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.EVENT_ID, eventId);
        return ElasticSearchUtils.getAll(this, queryBuilder, EventDataUtils.getEventKey(eventId));
    }

    public List<ChannelSessionAgencyData> getOccupationFieldsByEventAndChannelId(Long eventId, Long channelId, Long agencyId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.EVENT_ID, eventId);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.CHANNEL_ID, channelId);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.AGENCY_ID, agencyId);
        return ElasticSearchUtils.getAllFilteredFields(this, queryBuilder, EventDataUtils.getEventKey(eventId),
                List.of(ChannelSessionAgencyElasticProperty.SESSION_ID.getProperty(),
                        ChannelSessionAgencyElasticProperty.SOLD_OUT.getProperty(),
                        ChannelSessionAgencyElasticProperty.PRICES_MIN_BASE.getProperty(),
                        ChannelSessionAgencyElasticProperty.PRICES_MAX_BASE.getProperty(),
                        ChannelSessionAgencyElasticProperty.PRICES_MIN_BASE_PROMOTED.getProperty(),
                        ChannelSessionAgencyElasticProperty.PRICES_MIN_NET.getProperty(),
                        ChannelSessionAgencyElasticProperty.PRICES_MAX_NET.getProperty(),
                        ChannelSessionAgencyElasticProperty.PRICES_MIN_NET_PROMOTED.getProperty(),
                        ChannelSessionAgencyElasticProperty.PRICES_MIN_FINAL.getProperty(),
                        ChannelSessionAgencyElasticProperty.PRICES_MAX_FINAL.getProperty(),
                        ChannelSessionAgencyElasticProperty.PRICES_MIN_FINAL_PROMOTED.getProperty())
        );
    }

    public List<ChannelSessionAgencyData> getBySessionAndChannelId(List<Long> sessionIds, Long channelId, Long agencyId) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerms(queryBuilder, ChannelSessionAgencyElasticProperty.SESSION_ID, sessionIds);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.CHANNEL_ID, channelId);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.AGENCY_ID, agencyId);
        return ElasticSearchUtils.getAll(this, queryBuilder, null);
    }

}
