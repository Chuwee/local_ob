package es.onebox.event.catalog.elasticsearch.properties;

public enum ChannelEventAgencyElasticProperty implements ElasticProperty {

    ID("_id"),
    AGENCY_ID("channelEventAgency.agencyId"),
    CHANNEL_ID("channelEventAgency.channelId"),
    EVENT_ID("channelEventAgency.eventId"),
    PUBLISHED("channelEventAgency.publishChannelEvent"),
    CUSTOM_TAXONOMY_ID("channelEventAgency.customTaxonomyId"),
    CATALOG_INFO("channelEventAgency.catalogInfo"),
    CATALOG_FOR_SALE("channelEventAgency.catalogInfo.forSale"),
    CATALOG_SOLD_OUT("channelEventAgency.catalogInfo.soldOut"),
    CATALOG_ON_CATALOG("channelEventAgency.catalogInfo.onCatalog"),
    CATALOG_POSITION("channelEventAgency.catalogInfo.catalogPosition"),
    CATALOG_ON_CAROUSEL("channelEventAgency.catalogInfo.onCarousel"),
    CATALOG_CAROUSEL_POSITION("channelEventAgency.catalogInfo.carouselPosition"),
    CATALOG_HIGHLIGHTED("channelEventAgency.catalogInfo.highlighted"),
    CATALOG_PUBLISH_DATE("channelEventAgency.catalogInfo.date.publish"),
    CATALOG_START_DATE("channelEventAgency.catalogInfo.date.start"),
    CATALOG_END_DATE("channelEventAgency.catalogInfo.date.end"),
    CATALOG_SALE_START_DATE("channelEventAgency.catalogInfo.date.saleStart"),
    CATALOG_SALE_END_DATE("channelEventAgency.catalogInfo.date.saleEnd"),
    CATALOG_PRICES_MIN_PROMOTED("channelEventAgency.catalogInfo.prices.minPromotedPrice.value"),
    CATALOG_PRICES_MIN_BASE("channelEventAgency.catalogInfo.prices.minBasePrice.value"),
    CATALOG_PRICES_MAX_BASE("channelEventAgency.catalogInfo.prices.maxBasePrice.value");

    private String property;

    ChannelEventAgencyElasticProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
