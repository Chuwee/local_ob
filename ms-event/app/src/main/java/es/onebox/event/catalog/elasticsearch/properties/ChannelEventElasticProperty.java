package es.onebox.event.catalog.elasticsearch.properties;

public enum ChannelEventElasticProperty implements ElasticProperty {

    ID("_id"),
    CHANNEL_ID("channelEvent.channelId"),
    EVENT_ID("channelEvent.eventId"),
    PUBLISHED("channelEvent.publishChannelEvent"),
    CUSTOM_TAXONOMY_ID("channelEvent.customTaxonomyId"),
    CATALOG_INFO("channelEvent.catalogInfo"),
    CATALOG_FOR_SALE("channelEvent.catalogInfo.forSale"),
    CATALOG_SOLD_OUT("channelEvent.catalogInfo.soldOut"),
    CATALOG_ON_CATALOG("channelEvent.catalogInfo.onCatalog"),
    CATALOG_POSITION("channelEvent.catalogInfo.catalogPosition"),
    CATALOG_ON_CAROUSEL("channelEvent.catalogInfo.onCarousel"),
    CATALOG_CAROUSEL_POSITION("channelEvent.catalogInfo.carouselPosition"),
    CATALOG_HIGHLIGHTED("channelEvent.catalogInfo.highlighted"),
    CATALOG_PUBLISH_DATE("channelEvent.catalogInfo.date.publish"),
    CATALOG_START_DATE("channelEvent.catalogInfo.date.start"),
    CATALOG_END_DATE("channelEvent.catalogInfo.date.end"),
    CATALOG_SALE_START_DATE("channelEvent.catalogInfo.date.saleStart"),
    CATALOG_SALE_END_DATE("channelEvent.catalogInfo.date.saleEnd"),
    CATALOG_PRICES_MIN_PROMOTED("channelEvent.catalogInfo.prices.minPromotedPrice.value"),
    CATALOG_PRICES_MIN_BASE("channelEvent.catalogInfo.prices.minBasePrice.value"),
    CATALOG_PRICES_MAX_BASE("channelEvent.catalogInfo.prices.maxBasePrice.value");

    private String property;

    ChannelEventElasticProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
