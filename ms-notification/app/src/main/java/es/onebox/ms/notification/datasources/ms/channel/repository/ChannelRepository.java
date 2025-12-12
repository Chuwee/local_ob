package es.onebox.ms.notification.datasources.ms.channel.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.ms.notification.datasources.ms.channel.MsChannelDatasource;
import es.onebox.ms.notification.datasources.ms.channel.dto.Channel;
import es.onebox.ms.notification.datasources.ms.channel.dto.ChannelExternalToolsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ChannelRepository {
    private final MsChannelDatasource msChannelDatasource;

    @Autowired
    public ChannelRepository(MsChannelDatasource msChannelDatasource) {
        this.msChannelDatasource = msChannelDatasource;
    }

    @Cached(key = "entity", expires = 5 * 60)
    public Channel getChannel(@CachedArg Long channelId) {
        return msChannelDatasource.getChannel(channelId);
    }
    @Cached(key = "external-tools", expires = 5 * 60)
    public ChannelExternalToolsDTO getChannelExternalTools(@CachedArg Long channelId) {
        return msChannelDatasource.getChannelExternalTools(channelId);
    }
}
