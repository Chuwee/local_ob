package es.onebox.event.datasources.ms.channel.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.event.datasources.ms.channel.MsChannelDatasource;
import es.onebox.event.datasources.ms.channel.dto.ChannelConfigDTO;
import es.onebox.event.datasources.ms.channel.dto.attributes.ChannelAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class ChannelsRepository {

    private final MsChannelDatasource msChannelDatasource;

    @Autowired
    public ChannelsRepository(MsChannelDatasource msChannelDatasource) {
        this.msChannelDatasource = msChannelDatasource;
    }

    public ChannelAttributes getChannelAttributes(Long channelId) {
        return msChannelDatasource.getChannelAttributes(channelId);
    }

    @Cached(key = "ms-channel.channel-config", timeUnit = TimeUnit.MINUTES, expires = 3)
    public ChannelConfigDTO getChannelConfigCached(@CachedArg Long channelId) {
        return msChannelDatasource.getChannelConfig(channelId);
    }

}
