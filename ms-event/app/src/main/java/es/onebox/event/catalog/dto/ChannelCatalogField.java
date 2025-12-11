package es.onebox.event.catalog.dto;

import es.onebox.event.catalog.elasticsearch.properties.ElasticProperty;
import es.onebox.event.catalog.elasticsearch.properties.EventElasticProperty;

public enum ChannelCatalogField {
    EVENT_ID(EventElasticProperty.ID),
    EVENT_NAME(EventElasticProperty.NAME);

    private ElasticProperty mapping;

    ChannelCatalogField(ElasticProperty property) {
        this.mapping = property;
    }

    public ElasticProperty getMapping() {
        return mapping;
    }
}
