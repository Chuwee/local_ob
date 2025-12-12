package es.onebox.common.datasources.ms.channel.repository;

import es.onebox.common.datasources.ms.channel.MsChannelDatasource;
import es.onebox.common.datasources.ms.channel.dto.ChannelEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ChannelEventRepository {

    private final MsChannelDatasource msChannelDatasource;

    @Autowired
    public ChannelEventRepository(MsChannelDatasource msChannelDatasource) {
        this.msChannelDatasource = msChannelDatasource;
    }

    public ChannelEventDTO getChannelEventRelationship(Long channelId,Long eventId) {
        return msChannelDatasource.getChannelEventRelationship(channelId, eventId);
    }
}
