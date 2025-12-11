package es.onebox.mgmt.customdomains.channeldomain.domainchannels;

import es.onebox.mgmt.customdomains.common.converter.DomainSettingsConverter;
import es.onebox.mgmt.customdomains.common.dto.DomainSettingsDTO;
import es.onebox.mgmt.customdomains.common.dto.DomainSettings;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainChannelsSettingsService {

    private final ChannelsRepository channelsRepository;

    @Autowired
    public DomainChannelsSettingsService(ChannelsRepository channelsRepository) {
        this.channelsRepository = channelsRepository;
    }

    public DomainSettingsDTO get(Long channelId) {
        DomainSettings channelDomainSettings = channelsRepository.getChannelDomainSettings(channelId);
        return DomainSettingsConverter.toDTO(channelDomainSettings);
    }

    public void upsert( Long channelId, DomainSettingsDTO body) {
        DomainSettings channelDomainSettings = DomainSettingsConverter.fromDTO(body);
        channelsRepository.upsertChannelDomainSettings(channelId, channelDomainSettings);
    }

    public void disable( Long channelId) {
        channelsRepository.disableChannelDomainSettings(channelId);
    }
}
