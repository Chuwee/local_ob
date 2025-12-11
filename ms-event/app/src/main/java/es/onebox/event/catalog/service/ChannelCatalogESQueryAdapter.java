package es.onebox.event.catalog.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.elasticsearch.dao.Page;
import es.onebox.elasticsearch.utils.PageBuilder;
import es.onebox.event.catalog.dto.ChannelCatalogField;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventSessionsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogProductsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogSessionsFilter;
import es.onebox.event.catalog.dto.filter.SessionType;
import es.onebox.event.catalog.elasticsearch.properties.ChannelEventAgencyElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.ChannelEventElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.ChannelSessionAgencyElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.ChannelSessionElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.ElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.EventElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.EventVenueElasticProperty;
import es.onebox.event.catalog.elasticsearch.utils.ESBuilder;
import es.onebox.event.events.enums.EventType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ChannelCatalogESQueryAdapter {

    protected static final List<EventVenueElasticProperty> venueQueryStringFields;
    private static final Long TITLE_TAG_ID = 1L;
    private static final Long SUBTITLE_TAG_ID = 2L;


    static {
        venueQueryStringFields = new ArrayList<>();
        venueQueryStringFields.add(EventVenueElasticProperty.VENUES_MUNICIPALITY);
        venueQueryStringFields.add(EventVenueElasticProperty.VENUES_NAME);
    }

    protected static BoolQuery.Builder prepareChannelEventSessionsQuery(final Long channelId, final Long eventId,
                                                                        final ChannelCatalogEventSessionsFilter filter) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.CHANNEL_ID, channelId);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.EVENT_ID, eventId);
        if (filter != null) {
            ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.FOR_SALE, filter.getForSale());
            ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.SOLD_OUT, filter.getSoldOut());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionElasticProperty.START, filter.getStartDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionElasticProperty.END, filter.getEndDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionElasticProperty.START_LOCAL_DATE_TIME, filter.getStartLocalDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionElasticProperty.PUBLISH_DATE, filter.getPublishDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionElasticProperty.START_SALE_DATE, filter.getStartSaleDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionElasticProperty.END_SALE_DATE, filter.getEndSaleDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addMustTerms(queryBuilder, ChannelSessionElasticProperty.SESSION_ID, filter.getSessionId());
            ESBuilder.addMustTerms(queryBuilder, ChannelSessionElasticProperty.VENUE_CONFIG_ID, filter.getVenueConfigId());
            if (CollectionUtils.isNotEmpty(filter.getType()) && filter.getType().size() == 1) {
                if (SessionType.SESSION_PACK.equals(filter.getType().get(0))) {
                    ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.IS_SEASON_PACK_SESSION, true);
                } else {
                    ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.IS_SEASON_PACK_SESSION, false);
                }
            }
        }
        return queryBuilder;
    }

    protected static BoolQuery.Builder prepareChannelEventAgencySessionsQuery(final Long channelId, final Long eventId, Long agencyId,
                                                                        final ChannelCatalogEventSessionsFilter filter) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.CHANNEL_ID, channelId);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.EVENT_ID, eventId);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.AGENCY_ID, agencyId);

        if (filter != null) {
            ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.FOR_SALE, filter.getForSale());
            ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.SOLD_OUT, filter.getSoldOut());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionAgencyElasticProperty.START, filter.getStartDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionAgencyElasticProperty.END, filter.getEndDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionAgencyElasticProperty.START_LOCAL_DATE_TIME, filter.getStartLocalDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionAgencyElasticProperty.PUBLISH_DATE, filter.getPublishDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionAgencyElasticProperty.START_SALE_DATE, filter.getStartSaleDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionAgencyElasticProperty.END_SALE_DATE, filter.getEndSaleDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addMustTerms(queryBuilder, ChannelSessionAgencyElasticProperty.SESSION_ID, filter.getSessionId());
            ESBuilder.addMustTerms(queryBuilder, ChannelSessionAgencyElasticProperty.VENUE_CONFIG_ID, filter.getVenueConfigId());
            if (CollectionUtils.isNotEmpty(filter.getType()) && filter.getType().size() == 1) {
                if (SessionType.SESSION_PACK.equals(filter.getType().get(0))) {
                    ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.IS_SEASON_PACK_SESSION, true);
                } else {
                    ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.IS_SEASON_PACK_SESSION, false);
                }
            }
        }
        return queryBuilder;
    }

    public static BoolQuery.Builder prepareChannelSessionsQuery(final Long channelId, final ChannelCatalogSessionsFilter filter) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.CHANNEL_ID, channelId);
        if (filter != null) {
            if (filter.getEventId() != null) {
                ESBuilder.addMustTerms(queryBuilder, ChannelSessionElasticProperty.EVENT_ID, filter.getEventId());
            } else {
                ESBuilder.addMustExists(queryBuilder, ChannelSessionElasticProperty.EVENT_ID);
            }
            ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.FOR_SALE, filter.getForSale());
            ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.SOLD_OUT, filter.getSoldOut());
            ESBuilder.addMustTerms(queryBuilder, ChannelSessionElasticProperty.SESSION_ID, filter.getSessionId());
            ESBuilder.addMustTerms(queryBuilder, ChannelSessionElasticProperty.VENUE_CONFIG_ID, filter.getVenueConfigId());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionElasticProperty.START, filter.getStartDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionElasticProperty.END, filter.getEndDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionElasticProperty.PUBLISH_DATE, filter.getPublishDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionElasticProperty.START_SALE_DATE, filter.getStartSaleDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionElasticProperty.END_SALE_DATE, filter.getEndSaleDate(), zdt -> zdt.toInstant().toEpochMilli());
            if (CollectionUtils.isNotEmpty(filter.getType()) && filter.getType().size() == 1) {
                if (SessionType.SESSION_PACK.equals(filter.getType().get(0))) {
                    ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.IS_SEASON_PACK_SESSION, true);
                } else {
                    ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.IS_SEASON_PACK_SESSION, false);
                }
            }
        }
        return queryBuilder;
    }

    protected static BoolQuery.Builder prepareChannelAgencySessionsQuery(final Long channelId, final Long agencyId, final ChannelCatalogSessionsFilter filter) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.CHANNEL_ID, channelId);
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.AGENCY_ID, agencyId);
        if (filter != null) {
            if (filter.getEventId() != null) {
                ESBuilder.addMustTerms(queryBuilder, ChannelSessionAgencyElasticProperty.EVENT_ID, filter.getEventId());
            } else {
                ESBuilder.addMustExists(queryBuilder, ChannelSessionAgencyElasticProperty.EVENT_ID);
            }
            ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.FOR_SALE, filter.getForSale());
            ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.SOLD_OUT, filter.getSoldOut());
            ESBuilder.addMustTerms(queryBuilder, ChannelSessionAgencyElasticProperty.SESSION_ID, filter.getSessionId());
            ESBuilder.addMustTerms(queryBuilder, ChannelSessionAgencyElasticProperty.VENUE_CONFIG_ID, filter.getVenueConfigId());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionAgencyElasticProperty.START, filter.getStartDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionAgencyElasticProperty.END, filter.getEndDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionAgencyElasticProperty.PUBLISH_DATE, filter.getPublishDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionAgencyElasticProperty.START_SALE_DATE, filter.getStartSaleDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionAgencyElasticProperty.END_SALE_DATE, filter.getEndSaleDate(), zdt -> zdt.toInstant().toEpochMilli());
            if (CollectionUtils.isNotEmpty(filter.getType()) && filter.getType().size() == 1) {
                ESBuilder.addMustTerm(queryBuilder, ChannelSessionAgencyElasticProperty.IS_SEASON_PACK_SESSION, SessionType.SESSION_PACK.equals(filter.getType().get(0)));
            }
        }
        return queryBuilder;
    }

    protected static BoolQuery.Builder prepareChannelAgencyEventsQuery(Long channelId, Long agencyId, ChannelCatalogEventsFilter filter) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        queryBuilder.must(QueryBuilders.exists(exists -> exists.field(ChannelEventAgencyElasticProperty.CATALOG_INFO.getProperty())));
        ESBuilder.addMustTerm(queryBuilder, ChannelEventAgencyElasticProperty.CHANNEL_ID, channelId);
        ESBuilder.addMustTerm(queryBuilder, ChannelEventAgencyElasticProperty.AGENCY_ID, agencyId);

        if (filter != null) {
            ESBuilder.addMustTerm(queryBuilder, ChannelEventAgencyElasticProperty.CATALOG_ON_CATALOG, filter.getOnCatalog());
            ESBuilder.addMustTerm(queryBuilder, ChannelEventAgencyElasticProperty.CATALOG_ON_CAROUSEL, filter.getOnCarousel());
            ESBuilder.addMustTerm(queryBuilder, ChannelEventAgencyElasticProperty.CATALOG_FOR_SALE, filter.getForSale());
            ESBuilder.addMustTerm(queryBuilder, ChannelEventAgencyElasticProperty.CATALOG_SOLD_OUT, filter.getSoldOut());
            ESBuilder.addMustTerms(queryBuilder, ChannelEventAgencyElasticProperty.EVENT_ID, filter.getEventId());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelEventAgencyElasticProperty.CATALOG_PUBLISH_DATE, filter.getPublishDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelEventAgencyElasticProperty.CATALOG_START_DATE, filter.getStartDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelEventAgencyElasticProperty.CATALOG_END_DATE, filter.getEndDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelEventAgencyElasticProperty.CATALOG_SALE_START_DATE, filter.getStartSaleDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelEventAgencyElasticProperty.CATALOG_SALE_END_DATE, filter.getEndSaleDate(), zdt -> zdt.toInstant().toEpochMilli());
            addChannelEventPriceFilters(queryBuilder, filter.getPrice());
            addChannelEventPromotedPriceFilters(queryBuilder, filter.getPromotedPrice());
        }
        return queryBuilder;
    }

    protected static BoolQuery.Builder prepareChannelEventsQuery(Long channelId, ChannelCatalogEventsFilter filter) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        queryBuilder.must(QueryBuilders.exists(exists -> exists.field(ChannelEventElasticProperty.CATALOG_INFO.getProperty())));
        ESBuilder.addMustTerm(queryBuilder, ChannelEventElasticProperty.CHANNEL_ID, channelId);
        if (filter != null) {
            ESBuilder.addMustTerm(queryBuilder, ChannelEventElasticProperty.CATALOG_ON_CATALOG, filter.getOnCatalog());
            ESBuilder.addMustTerm(queryBuilder, ChannelEventElasticProperty.CATALOG_ON_CAROUSEL, filter.getOnCarousel());
            ESBuilder.addMustTerm(queryBuilder, ChannelEventElasticProperty.CATALOG_FOR_SALE, filter.getForSale());
            ESBuilder.addMustTerm(queryBuilder, ChannelEventElasticProperty.CATALOG_SOLD_OUT, filter.getSoldOut());
            ESBuilder.addMustTerms(queryBuilder, ChannelEventElasticProperty.EVENT_ID, filter.getEventId());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelEventElasticProperty.CATALOG_PUBLISH_DATE, filter.getPublishDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelEventElasticProperty.CATALOG_START_DATE, filter.getStartDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelEventElasticProperty.CATALOG_END_DATE, filter.getEndDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelEventElasticProperty.CATALOG_SALE_START_DATE, filter.getStartSaleDate(), zdt -> zdt.toInstant().toEpochMilli());
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelEventElasticProperty.CATALOG_SALE_END_DATE, filter.getEndSaleDate(), zdt -> zdt.toInstant().toEpochMilli());
            addChannelEventPriceFilters(queryBuilder, filter.getPrice());
            addChannelEventPromotedPriceFilters(queryBuilder, filter.getPromotedPrice());
        }

        return queryBuilder;
    }

    protected static BoolQuery.Builder prepareEventsQuery(ChannelCatalogEventsFilter filter) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        if (filter != null) {
            ESBuilder.addMustTerms(queryBuilder, EventElasticProperty.TYPE, prepareEventTypes(filter.getEventType()));
            ESBuilder.addMustTerms(queryBuilder, EventElasticProperty.ATTRIBUTE_VALUE_ID, filter.getAttributesValueId());
            ESBuilder.addNestedFilter(queryBuilder, EventVenueElasticProperty.VENUES_PATH, EventVenueElasticProperty.VENUES_ID, filter.getVenueId());
            ESBuilder.addNestedFilter(queryBuilder, EventVenueElasticProperty.VENUES_PATH, EventVenueElasticProperty.VENUES_COUNTRY_CODE, filter.getCountry());
            ESBuilder.addNestedFilter(queryBuilder, EventVenueElasticProperty.VENUES_PATH, EventVenueElasticProperty.VENUES_PROVINCE_CODE, filter.getCountrySubdivision());
            ESBuilder.addNestedQueryStringFilter(queryBuilder, EventVenueElasticProperty.VENUES_PATH, EventVenueElasticProperty.VENUES_MUNICIPALITY, filter.getCity());
            ESBuilder.addShouldQueryStringFilter(queryBuilder, Collections.singletonList(EventElasticProperty.NAME), filter.getQ());
            ESBuilder.addShouldNestedConditionedQueryStringFilter(queryBuilder, EventElasticProperty.COMMUNICATION_ELEMENT, EventElasticProperty.COMMUNICATION_ELEMENT_TAG_ID.getProperty(),
                    List.of(TITLE_TAG_ID, SUBTITLE_TAG_ID), EventElasticProperty.COMMUNICATION_ELEMENT_VALUE.getProperty(), filter.getQ());
            ESBuilder.addShouldNestedQueryStringFilter(queryBuilder, EventVenueElasticProperty.VENUES_PATH, venueQueryStringFields, filter.getQ());
            if (filter.getCategoryCode() != null) {
                ESBuilder.shouldByTwoFields(queryBuilder, EventElasticProperty.TAXONOMY_CODE.getProperty(), filter.getCategoryCode(), EventElasticProperty.CUSTOM_TAXONOMY_CODE.getProperty(), filter.getCategoryCode());
            }
            if (filter.getCustomCategoryCode() != null) {
                ESBuilder.addMustMatch(queryBuilder, EventElasticProperty.CUSTOM_TAXONOMY_CODE, filter.getCustomCategoryCode());
            }
        }
        return queryBuilder;
    }

    protected static BoolQuery.Builder prepareChannelSessionQuery(ChannelCatalogEventsFilter filter) {
        BoolQuery.Builder queryBuilder = null;
        if (filter != null && filter.getSessionsStartDate() != null) {
            queryBuilder = QueryBuilders.bool();
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionElasticProperty.START, filter.getSessionsStartDate(), zdt -> zdt.toInstant().toEpochMilli());
        }

        return queryBuilder;
    }

    protected static BoolQuery.Builder prepareChannelSessionAgencyQuery(ChannelCatalogEventsFilter filter) {
        BoolQuery.Builder queryBuilder = null;
        if (filter != null && filter.getSessionsStartDate() != null) {
            queryBuilder = QueryBuilders.bool();
            ESBuilder.addFiltersWithOperator(queryBuilder, ChannelSessionAgencyElasticProperty.START,
                    filter.getSessionsStartDate(), zdt -> zdt.toInstant().toEpochMilli());
        }

        return queryBuilder;
    }

    protected static void addChannelEventPromotedPriceFilters(BoolQuery.Builder queryBuilder,
                                                              List<FilterWithOperator<Double>> promotedPriceFilters) {
        if (CollectionUtils.isNotEmpty(promotedPriceFilters)) {
            promotedPriceFilters.forEach(filter -> addChannelEventPromotedPriceFilter(queryBuilder, filter));
        }
    }

    protected static void addChannelEventPromotedPriceFilter(BoolQuery.Builder queryBuilder,
                                                             FilterWithOperator<Double> promotedPriceFilter) {

        ESBuilder.addMustExists(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MIN_PROMOTED);

        if (Operator.GREATER_THAN == promotedPriceFilter.getOperator()) {
            ESBuilder.addMustRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MAX_BASE, Operator.GREATER_THAN, promotedPriceFilter.getValue());
        } else if (Operator.GREATER_THAN_OR_EQUALS == promotedPriceFilter.getOperator()) {
            ESBuilder.addMustRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MAX_BASE, Operator.GREATER_THAN_OR_EQUALS, promotedPriceFilter.getValue());
        } else if (Operator.LESS_THAN == promotedPriceFilter.getOperator()) {
            ESBuilder.addMustRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MIN_PROMOTED, Operator.LESS_THAN, promotedPriceFilter.getValue());
        } else if (Operator.LESS_THAN_OR_EQUALS == promotedPriceFilter.getOperator()) {
            ESBuilder.addMustRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MIN_PROMOTED, Operator.LESS_THAN_OR_EQUALS, promotedPriceFilter.getValue());
        } else if (Operator.EQUALS == promotedPriceFilter.getOperator()) {
            ESBuilder.addMustRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MIN_PROMOTED, Operator.LESS_THAN_OR_EQUALS, promotedPriceFilter.getValue());
            ESBuilder.addMustRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MAX_BASE, Operator.GREATER_THAN_OR_EQUALS, promotedPriceFilter.getValue());
        } else if (Operator.NOT_EQUALS == promotedPriceFilter.getOperator()) {
            ESBuilder.addShouldRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MIN_PROMOTED, Operator.GREATER_THAN, promotedPriceFilter.getValue());
            ESBuilder.addShouldRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MAX_BASE, Operator.LESS_THAN, promotedPriceFilter.getValue());
        } else {
            throw new IllegalArgumentException("Cannot add a range query filter with operator " + promotedPriceFilter.getOperator());
        }
    }

    protected static void addChannelEventPriceFilters(BoolQuery.Builder queryBuilder, List<FilterWithOperator<Double>> priceFilters) {
        if (CollectionUtils.isNotEmpty(priceFilters)) {
            priceFilters.forEach(filter -> addChannelEventPriceFilter(queryBuilder, filter));
        }
    }

    protected static String[] prepareFields(ChannelCatalogEventsFilter filter) {
        String[] fields = null;
        if (CollectionUtils.isNotEmpty(filter.getField())) {
            fields = filter.getField().stream().map(ChannelCatalogField::getMapping).map(ElasticProperty::getProperty)
                    .toArray(String[]::new);
        }
        return fields;
    }

    protected static void addChannelEventPriceFilter(BoolQuery.Builder queryBuilder, FilterWithOperator<Double> priceFilter) {
        if (Operator.GREATER_THAN == priceFilter.getOperator()) {
            ESBuilder.addMustRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MAX_BASE, Operator.GREATER_THAN, priceFilter.getValue());
        } else if (Operator.GREATER_THAN_OR_EQUALS == priceFilter.getOperator()) {
            ESBuilder.addMustRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MAX_BASE, Operator.GREATER_THAN_OR_EQUALS, priceFilter.getValue());
        } else if (Operator.LESS_THAN == priceFilter.getOperator()) {
            ESBuilder.addMustRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MIN_BASE, Operator.LESS_THAN, priceFilter.getValue());
        } else if (Operator.LESS_THAN_OR_EQUALS == priceFilter.getOperator()) {
            ESBuilder.addMustRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MIN_BASE, Operator.LESS_THAN_OR_EQUALS, priceFilter.getValue());
        } else if (Operator.EQUALS == priceFilter.getOperator()) {
            ESBuilder.addMustRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MIN_BASE, Operator.LESS_THAN_OR_EQUALS, priceFilter.getValue());
            ESBuilder.addMustRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MAX_BASE, Operator.GREATER_THAN_OR_EQUALS, priceFilter.getValue());
        } else if (Operator.NOT_EQUALS == priceFilter.getOperator()) {
            ESBuilder.addShouldRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MIN_BASE, Operator.GREATER_THAN, priceFilter.getValue());
            ESBuilder.addShouldRange(queryBuilder, ChannelEventElasticProperty.CATALOG_PRICES_MAX_BASE, Operator.LESS_THAN, priceFilter.getValue());
        } else {
            throw new IllegalArgumentException("Cannot add a range query filter with operator " + priceFilter.getOperator());
        }
    }

    public static Page preparePage(ChannelCatalogFilter filter) {
        Long offset = filter.getOffset();
        if (offset == null) {
            offset = 0L;
            filter.setOffset(offset);
        }
        Long limit = filter.getLimit();
        if (limit == null) {
            limit = ChannelCatalogFilter.DEFAULT_MAX_LIMIT;
            filter.setLimit(limit);
        }
        return new PageBuilder(offset, limit).build();
    }

    protected static Page preparePage(ChannelCatalogProductsFilter filter) {
        Long offset = filter.getOffset();
        if (offset == null) {
            offset = 0L;
            filter.setOffset(offset);
        }
        Long limit = filter.getLimit();
        if (limit == null) {
            limit = ChannelCatalogFilter.DEFAULT_MAX_LIMIT;
            filter.setLimit(limit);
        }
        return new PageBuilder(offset, limit).build();
    }


    protected static BoolQuery.Builder prepareChannelProductsQuery(final Long channelId, final ChannelCatalogProductsFilter filter) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        ESBuilder.addMustTerm(queryBuilder, ChannelSessionElasticProperty.CHANNEL_ID, channelId);
        if (filter != null) {
            if (filter.getEventId() != null) {
                ESBuilder.addMustTerms(queryBuilder, ChannelSessionElasticProperty.EVENT_ID, filter.getEventId());
            } else {
                ESBuilder.addMustExists(queryBuilder, ChannelSessionElasticProperty.EVENT_ID);
            }
            ESBuilder.addMustTerms(queryBuilder, ChannelSessionElasticProperty.SESSION_ID, filter.getSessionId());
        }
        return queryBuilder;
    }

    private static List<Integer> prepareEventTypes(List<EventType> eventTypes) {
        if (CollectionUtils.isEmpty(eventTypes)) {
            return null;
        }
        return eventTypes.stream().map(EventType::getId).collect(Collectors.toList());
    }
}
