package es.onebox.mgmt.customdomains.channeldomain.cors;

import es.onebox.mgmt.customdomains.channeldomain.cors.converter.CorsSettingsConverter;
import es.onebox.mgmt.customdomains.channeldomain.cors.dto.CorsSettingsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCorsSettings;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CorsSettingsService {

    private final ChannelsRepository channelsRepository;

    @Autowired
    public CorsSettingsService(ChannelsRepository channelsRepository) {
        this.channelsRepository = channelsRepository;
    }

    public CorsSettingsDTO get(Long channelId) {
        ChannelCorsSettings channelCorsSettings = channelsRepository.getChannelCorsSettings(channelId);
        return CorsSettingsConverter.toDTO(channelCorsSettings);
    }

    public void upsert(Long channelId, CorsSettingsDTO body) {
        ChannelCorsSettings channelCorsSettings = CorsSettingsConverter.fromDTO(body);
        channelsRepository.upsertChannelCorsSettings(channelId, channelCorsSettings);
    }

    public void disable(Long channelId) {
        channelsRepository.disableChannelCorsSettings(channelId);
    }
}
