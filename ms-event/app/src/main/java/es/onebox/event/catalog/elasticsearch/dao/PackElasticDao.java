package es.onebox.event.catalog.elasticsearch.dao;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.elasticsearch.dao.AbstractElasticDao;
import es.onebox.elasticsearch.dao.Page;
import es.onebox.elasticsearch.utils.PageBuilder;
import es.onebox.event.catalog.elasticsearch.dto.ElasticSearchResults;
import es.onebox.event.catalog.elasticsearch.utils.ESBuilder;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackFilter;
import es.onebox.event.catalog.elasticsearch.dto.pack.PackData;
import es.onebox.event.catalog.elasticsearch.properties.ChannelPackElasticProperty;
import es.onebox.event.catalog.elasticsearch.utils.PackDataUtils;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.packs.enums.PackType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;


@Repository
public class PackElasticDao extends AbstractElasticDao<PackData> {

    public void upsertChannelPack(ChannelPack channelPack) {
        PackData packData = new PackData();
        packData.setId(PackDataUtils.getChannelPackKey(channelPack.getChannelId(), channelPack.getId()));
        packData.setChannelPack(channelPack);
        upsert(packData);
    }

    public ElasticSearchResults<PackData> searchChannelPacks(Long channelId, ChannelPackFilter filter) {
        BoolQuery.Builder packQuery = prepareChannelPackQuery(channelId, filter);
        Page page = preparePage(filter);
        return searchChannelPacks(packQuery, page);
    }

    private ElasticSearchResults<PackData> searchChannelPacks(BoolQuery.Builder channelPackQuery, Page page) {
        Query query = channelPackQuery.build()._toQuery();
        SearchResponse<PackData> searchResponse = query(query, page);

        List<PackData> results = searchResponse.hits().hits().stream()
                .map(this::fromHit)
                .toList();

        return new ElasticSearchResults<>(getMetadata(page, searchResponse), results);
    }

    private PackData fromHit(Hit<PackData> hit) {
        PackData data = new PackData();
        data.setId(hit.id());
        if (hit.source() != null) {
            data.setChannelPack(hit.source().getChannelPack());
        }
        return data;
    }

    private static Metadata getMetadata(Page page, SearchResponse<PackData> searchResponse) {
        Metadata metadata = new Metadata();
        metadata.setOffset((long) page.getFromItem());
        metadata.setLimit((long) page.getSize());
        TotalHits totalHits = searchResponse.hits().total();
        metadata.setTotal(totalHits != null ? totalHits.value() : 0);
        return metadata;
    }

    private static BoolQuery.Builder prepareChannelPackQuery(Long channelId, ChannelPackFilter filter) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelPackElasticProperty.CHANNEL_ID, channelId);
        if (filter != null) {
            ESBuilder.addMustTerm(queryBuilder, ChannelPackElasticProperty.ON_SALE, filter.getOnSale());
            ESBuilder.addMustTerm(queryBuilder, ChannelPackElasticProperty.FOR_SALE, filter.getForSale());
            ESBuilder.addMustMatch(queryBuilder, ChannelPackElasticProperty.CUSTOM_CATEGORY_CODE, filter.getCustomCategoryCode());
            ESBuilder.addShouldQueryStringFilter(queryBuilder, Collections.singletonList(ChannelPackElasticProperty.NAME), filter.getQ());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelPackElasticProperty.START_DATE, filter.getStartDate());

            //TODO when the manual packs have been developed, add type to the filter and replace the automatic pack type value
            ESBuilder.addMustTerm(queryBuilder, ChannelPackElasticProperty.TYPE, PackType.AUTOMATIC.name());
            ESBuilder.addMustTerm(queryBuilder, ChannelPackElasticProperty.SUGGESTED, filter.getSuggested());

            if (CollectionUtils.isNotEmpty(filter.getEventId()) && CollectionUtils.isNotEmpty(filter.getSessionId())) {
                ESBuilder.addMustNested(queryBuilder, ChannelPackElasticProperty.CHANNEL_PACK_ITEMS, filterByEventOrSessionItemPack(filter));
            } else if (CollectionUtils.isNotEmpty(filter.getEventId())) {
                BoolQuery.Builder query = filterItemsByIdsCondition(filter.getEventId(), PackItemType.EVENT);
                addMainFilter(query, filter);
                ESBuilder.addMustNested(queryBuilder, ChannelPackElasticProperty.CHANNEL_PACK_ITEMS, query.build()._toQuery());
            } else if (CollectionUtils.isNotEmpty(filter.getSessionId())) {
                BoolQuery.Builder query = filterItemsByIdsCondition(filter.getSessionId(), PackItemType.SESSION);
                addMainFilter(query, filter);
                ESBuilder.addMustNested(queryBuilder, ChannelPackElasticProperty.CHANNEL_PACK_ITEMS, query.build()._toQuery());
            }
        }
        return queryBuilder;
    }

    private static void addMainFilter(BoolQuery.Builder query, ChannelPackFilter filter) {
        if (filter.getMain() != null) {
            query.must(QueryBuilders.term(t -> t.field(ChannelPackElasticProperty.MAIN_ITEM.getProperty()).value(filter.getMain())));
        }
    }

    private static Query filterByEventOrSessionItemPack(ChannelPackFilter filter) {
        BoolQuery.Builder query = QueryBuilders.bool()
                .should(filterItemsByIdsCondition(filter.getEventId(), PackItemType.EVENT).build()._toQuery())
                .should(filterItemsByIdsCondition(filter.getSessionId(), PackItemType.SESSION).build()._toQuery())
                .minimumShouldMatch("1");
        addMainFilter(query, filter);
        return QueryBuilders.bool()
                .must(query.build()._toQuery())
                .build()._toQuery();
    }

    private static BoolQuery.Builder filterItemsByIdsCondition(List<Long> ids, PackItemType itemType) {
        BoolQuery.Builder query = QueryBuilders.bool();
        ESBuilder.addMustTerm(query, ChannelPackElasticProperty.ITEM_TYPE, itemType.name());
        if (ids.size() == 1) {
            ESBuilder.addMustTerm(query, ChannelPackElasticProperty.ITEM_ID, ids.get(0));
        } else {
            ESBuilder.addMustTerms(query, ChannelPackElasticProperty.ITEM_ID, ids);
        }
        return query;
    }

    private static Page preparePage(ChannelPackFilter filter) {
        Long offset = filter.getOffset() != null ? filter.getOffset() : 0L;
        Long limit = filter.getLimit() != null ? filter.getLimit() : ChannelPackFilter.DEFAULT_MAX_LIMIT;

        filter.setOffset(offset);
        filter.setLimit(limit);

        return new PageBuilder(offset, limit).build();
    }

}
