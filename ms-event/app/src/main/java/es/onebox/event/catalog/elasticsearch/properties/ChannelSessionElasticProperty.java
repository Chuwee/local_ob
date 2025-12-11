package es.onebox.event.catalog.elasticsearch.properties;

public enum ChannelSessionElasticProperty implements ElasticProperty {

    EVENT_ID("channelSession.eventId"),
    SESSION_ID("channelSession.sessionId"),
    CHANNEL_ID("channelSession.channelId"),
    FOR_SALE("channelSession.forSale"),
    SOLD_OUT("channelSession.soldOut"),
    PUBLISH_DATE("channelSession.date.publish"),
    START_SALE_DATE("channelSession.date.saleStart"),
    END_SALE_DATE("channelSession.date.saleEnd"),
    START("channelSession.date.start"),
    END("channelSession.date.end"),
    START_LOCAL_DATE_TIME("channelSession.date.startLocalDate"),
    PRICES("channelSession.prices"),
    PRICES_MIN_BASE("channelSession.prices.minBasePrice.value"),
    PRICES_MAX_BASE("channelSession.prices.maxBasePrice.value"),
    PRICES_MIN_BASE_PROMOTED("channelSession.prices.minPromotedPrices"),
    PRICES_MIN_NET("channelSession.prices.minNetPrice.value"),
    PRICES_MAX_NET("channelSession.prices.maxNetPrice.value"),
    PRICES_MIN_NET_PROMOTED("channelSession.prices.minNetPromotedPrices"),
    PRICES_MIN_FINAL("channelSession.prices.minFinalPrice.value"),
    PRICES_MAX_FINAL("channelSession.prices.maxFinalPrice.value"),
    PRICES_MIN_FINAL_PROMOTED("channelSession.prices.minFinalPromotedPrices"),
    VENUE_CONFIG_ID("channelSession.venueConfigId"),
    IS_SEASON_PACK_SESSION("channelSession.seasonPackSession");

    private String property;

    ChannelSessionElasticProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
