package es.onebox.channels.catalog;

import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;

import java.util.Map;

public interface ChannelCatalogConverter<T extends ChannelCatalog> {

    T convert(ChannelCatalogContext context, Long limit, Long offset, Map parameters);

    default String getLimitParameter() {
        return "limit";
    }

    default String getOffsetParameter() {
        return "offset";
    }

}
