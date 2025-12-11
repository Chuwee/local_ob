package es.onebox.mgmt.channels.blacklists;

import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.blacklists.converter.ChannelBlacklistsConverter;
import es.onebox.mgmt.channels.blacklists.dto.ChannelBlacklistDTO;
import es.onebox.mgmt.channels.blacklists.dto.ChannelBlacklistStatusDTO;
import es.onebox.mgmt.channels.blacklists.dto.ChannelBlacklistsDTO;
import es.onebox.mgmt.channels.blacklists.dto.ChannelBlacklistsResponseDTO;
import es.onebox.mgmt.channels.blacklists.enums.ChannelBlacklistType;
import es.onebox.mgmt.channels.blacklists.filter.ChannelBlacklistFilterDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklist;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistStatus;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistsResponse;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelBlacklistsService {

    private ChannelsRepository channelsRepository;
    private ChannelsHelper channelsHelper;

    @Autowired
    public ChannelBlacklistsService(ChannelsRepository channelsRepository, ChannelsHelper channelsHelper) {
        this.channelsRepository = channelsRepository;
        this.channelsHelper = channelsHelper;
    }

    public ChannelBlacklistStatusDTO getBlacklistStatus(Long channelId, ChannelBlacklistType type) {
        validateChannel(channelId);
        ChannelBlacklistStatus response = channelsRepository.getChannelBlacklistStatus(channelId, ChannelBlacklistsConverter.toMs(type));
        return ChannelBlacklistsConverter.toDTO(response);
    }

    public void updateBlacklistStatus(Long channelId, ChannelBlacklistType type, ChannelBlacklistStatusDTO request) {
        validateChannel(channelId);
        ChannelBlacklistStatus msRequest = ChannelBlacklistsConverter.toMs(request);
        channelsRepository.updateChannelBlacklistStatus(channelId, ChannelBlacklistsConverter.toMs(type), msRequest);
    }

    public ChannelBlacklistsResponseDTO getBlacklists(Long channelId, ChannelBlacklistType type, ChannelBlacklistFilterDTO filter) {
        validateChannel(channelId);
        es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistType msType = ChannelBlacklistsConverter.toMs(type);
        ChannelBlacklistFilter msFilter = ChannelBlacklistsConverter.toMs(filter);
        ChannelBlacklistsResponse response = channelsRepository.getChannelBlacklist(channelId, msType, msFilter);
        return ChannelBlacklistsConverter.toDTO(response);
    }

    public ChannelBlacklistDTO getBlacklistItem(Long channelId, ChannelBlacklistType type, String value) {
        validateChannel(channelId);
        es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistType msType = ChannelBlacklistsConverter.toMs(type);
        ChannelBlacklist response = channelsRepository.getChannelBlacklistItem(channelId, msType, value);
        return ChannelBlacklistsConverter.toDTO(response);
    }

    public void createBlacklists(Long channelId, ChannelBlacklistType type, ChannelBlacklistsDTO body) {
        validateChannel(channelId);
        es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistType msType = ChannelBlacklistsConverter.toMs(type);
        List<ChannelBlacklist> request = ChannelBlacklistsConverter.toMs(channelId.intValue(), type, body);
        channelsRepository.createChannelBlacklists(channelId, msType, request);
    }

    public void deleteBlacklists(Long channelId, ChannelBlacklistType type, ChannelBlacklistFilterDTO filter) {
        validateChannel(channelId);
        es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistType msType = ChannelBlacklistsConverter.toMs(type);
        ChannelBlacklistFilter msFilter = ChannelBlacklistsConverter.toMs(filter);
        channelsRepository.deleteChannelBlacklists(channelId, msType, msFilter);
    }

    public void deleteBlacklistItem(Long channelId, ChannelBlacklistType type, String value) {
        validateChannel(channelId);
        es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistType msType = ChannelBlacklistsConverter.toMs(type);
        channelsRepository.deleteChannelBlacklistItem(channelId, msType, value);
    }

    private void validateChannel(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
    }
}

