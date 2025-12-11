package es.onebox.event.events.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortableField;
import es.onebox.event.catalog.elasticsearch.properties.ChannelEventElasticProperty;

public enum CatalogSortableField implements SortableField {
    @JsonProperty("custom_order")
    CUSTOM_ORDER(ChannelEventElasticProperty.CATALOG_POSITION.getProperty()),

    @JsonProperty("carousel_position")
    CAROUSEL_POSITION(ChannelEventElasticProperty.CATALOG_CAROUSEL_POSITION.getProperty());

    private final String name;

    CatalogSortableField(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}