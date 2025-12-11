package es.onebox.mgmt.channels.customresources.assets;

import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.customresources.assets.converter.ChannelCustomResourceAssetsConverter;
import es.onebox.mgmt.channels.customresources.assets.dto.CreateCustomResourceAssetsDTO;
import es.onebox.mgmt.channels.customresources.assets.dto.CustomResourceAssetsDTO;
import es.onebox.mgmt.channels.customresources.assets.dto.CustomResourceAssetsFilter;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import org.springframework.stereotype.Service;

@Service
public class ChannelCustomResourceAssetsService {

    private final ChannelsHelper channelsHelper;
    private final ChannelsRepository channelsRepository;

    public ChannelCustomResourceAssetsService(ChannelsHelper channelsHelper, ChannelsRepository channelsRepository) {
        this.channelsHelper = channelsHelper;
        this.channelsRepository = channelsRepository;
    }

    public CustomResourceAssetsDTO searchCustomResourceAssets(Integer channelId, CustomResourceAssetsFilter customResourceAssetsFilter) {
        channelsHelper.getAndCheckChannel(channelId.longValue());
        return ChannelCustomResourceAssetsConverter.fromMs(
                channelsRepository.getCustomResourceAssets(channelId,
                        ChannelCustomResourceAssetsConverter.toMs(customResourceAssetsFilter))
        );
    }

    public void addCustomResourceAssets(Integer channelId, CreateCustomResourceAssetsDTO body) {
        channelsHelper.getAndCheckChannel(channelId.longValue());
        channelsRepository.addCustomResourceAssets(channelId, ChannelCustomResourceAssetsConverter.toMs(body));
    }

    public void deleteCustomResourceAsset(Integer channelId, String filename) {
        channelsHelper.getAndCheckChannel(channelId.longValue());
        channelsRepository.deleteCustomResourceAsset(channelId, filename);
    }
}
