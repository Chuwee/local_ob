package es.onebox.mgmt.channels.externaltools;

import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.externaltools.converter.ChannelExternalToolsConverter;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolsDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolsNamesDTO;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalTool;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalTools;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelExternalToolsService {

    private final ChannelsHelper channelsHelper;

    private final ChannelsRepository channelsRepository;

    @Autowired
    ChannelExternalToolsService(ChannelsHelper channelsHelper, ChannelsRepository channelsRepository) {
        this.channelsHelper = channelsHelper;
        this.channelsRepository = channelsRepository;
    }

    public ChannelExternalToolsDTO getById(Long channelId) {
        validateChannel(channelId);
        ChannelExternalTools response = channelsRepository.getChannelExternalTools(channelId);
        return ChannelExternalToolsConverter.fromMs(response);
    }

    public void updateByChannelId(Long channelId, ChannelExternalToolDTO request, ChannelExternalToolsNamesDTO toolName) {
        validateChannel(channelId);
        ChannelExternalTool msRequest = ChannelExternalToolsConverter.toMs(request, toolName);
        channelsRepository.updateChannelExternalTools(channelId, msRequest);
    }

    public void resetExternalTool(Long channelId, ChannelExternalToolsNamesDTO toolName) {
        validateChannel(channelId);
        channelsRepository.resetExternalTool(channelId, toolName);
    }

    private void validateChannel(Long channelId) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        ChannelUtils.validateOBPortalOrMembers(channelResponse.getType());
    }

}