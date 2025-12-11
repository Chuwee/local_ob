package es.onebox.event.catalog.elasticsearch.dto.pack;


import es.onebox.elasticsearch.annotation.ElasticRepository;
import es.onebox.elasticsearch.dao.ElasticDocument;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import es.onebox.event.catalog.elasticsearch.utils.PackDataUtils;

import java.io.Serial;
import java.io.Serializable;

@ElasticRepository(indexName = PackDataUtils.PACK_INDEX, queryLimit = 10000)
public class PackData implements ElasticDocument, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private ChannelPack channelPack;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChannelPack getChannelPack() {
        return channelPack;
    }

    public void setChannelPack(ChannelPack channelPack) {
        this.channelPack = channelPack;
    }
}
