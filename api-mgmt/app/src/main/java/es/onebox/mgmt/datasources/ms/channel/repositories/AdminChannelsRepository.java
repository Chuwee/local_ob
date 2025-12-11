package es.onebox.mgmt.datasources.ms.channel.repositories;

import es.onebox.mgmt.datasources.ms.channel.MsChannelDatasource;
import es.onebox.mgmt.datasources.ms.channel.dto.AdminChannelsResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelUpdateRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.domainconfig.DomainConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AdminChannelsRepository {

    private final MsChannelDatasource msChannelDatasource;

    @Autowired
    public AdminChannelsRepository(MsChannelDatasource msChannelDatasource) {
        this.msChannelDatasource = msChannelDatasource;
    }

    public AdminChannelsResponse getAdminChannels(ChannelFilter filter) {
        return msChannelDatasource.getAdminChannels(filter);
    }

    public void migrateChannel(Long channelId, Boolean migrateToChannels, Boolean stripeHookChecked) {
        msChannelDatasource.migrateChannel(channelId, migrateToChannels, stripeHookChecked);
    }

    public void updateChannel(Long channelId, ChannelUpdateRequest request) {
        msChannelDatasource.updateChannel(channelId, request);
    }

    public DomainConfig getDomainConfig(String domain) {
        return msChannelDatasource.getDomainConfig(domain);
    }

    public void updateDomainConfig(String domain, DomainConfig body) {
        msChannelDatasource.updateDomainConfig(domain, body);
    }

}

