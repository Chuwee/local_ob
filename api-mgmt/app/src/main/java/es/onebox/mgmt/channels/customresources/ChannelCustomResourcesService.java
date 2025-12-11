package es.onebox.mgmt.channels.customresources;

import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.customresources.converter.ChannelCustomResourcesConverter;
import es.onebox.mgmt.channels.customresources.dto.CustomResourcesDTO;
import es.onebox.mgmt.channels.customresources.dto.UpdateCustomResourcesDTO;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import org.springframework.stereotype.Service;

@Service
public class ChannelCustomResourcesService {

    private final ChannelsHelper channelsHelper;
    private final ChannelsRepository channelsRepository;

    public ChannelCustomResourcesService(ChannelsHelper channelsHelper, ChannelsRepository channelsRepository) {
        this.channelsHelper = channelsHelper;
        this.channelsRepository = channelsRepository;
    }

    public CustomResourcesDTO getCustomResources(Integer channelId) {
        channelsHelper.getAndCheckChannel(channelId.longValue());
        return ChannelCustomResourcesConverter.fromMs(channelsRepository.getCustomResources(channelId));
    }

    public void upsertCustomResources(Integer channelId, UpdateCustomResourcesDTO updateCustomResourcesDTO) {
        channelsHelper.getAndCheckChannel(channelId.longValue());
        channelsRepository.createOrUpdateCustomResources(channelId, ChannelCustomResourcesConverter.toMs(updateCustomResourcesDTO));
    }
}
