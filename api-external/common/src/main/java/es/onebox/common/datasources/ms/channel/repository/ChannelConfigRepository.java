package es.onebox.common.datasources.ms.channel.repository;

import es.onebox.common.datasources.ms.channel.MsChannelDatasource;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ChannelConfigRepository {

    private final MsChannelDatasource msChannelDataSource;

    @Autowired
    public ChannelConfigRepository(MsChannelDatasource msChannelDatasource){
        this.msChannelDataSource = msChannelDatasource;
    }

    public ChannelConfigDTO getChannelConfig(Long channelId){
        return msChannelDataSource.getChannelConfig(channelId);
    }

    public ChannelConfigDTO getChannelConfigByPath(String channelPath){
        return msChannelDataSource.getChannelConfigByPath(channelPath);
    }

    public void updateChannelConfig(Long channelId, ChannelConfigDTO channelConfig){
        msChannelDataSource.putChannelConfig(channelId, channelConfig);
    }
}
