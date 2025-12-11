package es.onebox.event.catalog.elasticsearch.properties;

public enum ChannelSessionAgencyElasticProperty implements ElasticProperty {

    AGENCY_ID("channelSessionAgency.agencyId"),
    EVENT_ID("channelSessionAgency.eventId"),
    SESSION_ID("channelSessionAgency.sessionId"),
    CHANNEL_ID("channelSessionAgency.channelId"),
    FOR_SALE("channelSessionAgency.forSale"),
    SOLD_OUT("channelSessionAgency.soldOut"),
    PUBLISH_DATE("channelSessionAgency.date.publish"),
    START_SALE_DATE("channelSessionAgency.date.saleStart"),
    END_SALE_DATE("channelSessionAgency.date.saleEnd"),
    START("channelSessionAgency.date.start"),
    END("channelSessionAgency.date.end"),
    START_LOCAL_DATE_TIME("channelSessionAgency.date.startLocalDate"),
    PRICES("channelSessionAgency.prices"),
    PRICES_MIN_BASE("channelSessionAgency.prices.minBasePrice.value"),
    PRICES_MAX_BASE("channelSessionAgency.prices.maxBasePrice.value"),
    PRICES_MIN_BASE_PROMOTED("channelSessionAgency.prices.minPromotedPrices"),
    PRICES_MIN_NET("channelSessionAgency.prices.minNetPrice.value"),
    PRICES_MAX_NET("channelSessionAgency.prices.maxNetPrice.value"),
    PRICES_MIN_NET_PROMOTED("channelSessionAgency.prices.minNetPromotedPrices"),
    PRICES_MIN_FINAL("channelSessionAgency.prices.minFinalPrice.value"),
    PRICES_MAX_FINAL("channelSessionAgency.prices.maxFinalPrice.value"),
    PRICES_MIN_FINAL_PROMOTED("channelSessionAgency.prices.minFinalPromotedPrices"),
    VENUE_CONFIG_ID("channelSessionAgency.venueConfigId"),
    IS_SEASON_PACK_SESSION("channelSessionAgency.seasonPackSession");

    private String property;

    ChannelSessionAgencyElasticProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
