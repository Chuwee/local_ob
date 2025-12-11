package es.onebox.event.catalog.elasticsearch.properties;

public enum ChannelPackElasticProperty implements ElasticProperty {

    NAME("channelPack.name"),
    STATUS("channelPack.status"),
    CUSTOM_CATEGORY_CODE("channelPack.customCategoryCode"),
    START_DATE("channelPack.dates.start"),
    CHANNEL_ID("channelPack.channelId"),
    FOR_SALE("channelPack.forSale"),
    ON_SALE("channelPack.onSale"),
    TYPE("channelPack.type"),
    SUGGESTED("channelPack.suggested"),
    CHANNEL_PACK_ITEMS("channelPack.items"),
    ITEM_ID("channelPack.items.itemId"),
    MAIN_ITEM("channelPack.items.main"),
    ITEM_TYPE("channelPack.items.type");

    private final String property;

    ChannelPackElasticProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}