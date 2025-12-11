package es.onebox.mgmt.channels.tickettemplates;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.tickettemplates.dto.ChannelTemplateTicketType;
import es.onebox.mgmt.channels.tickettemplates.dto.ChannelTicketTemplateDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelUpdateRequest;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelTicketTemplatesService {

    @Autowired
    private ChannelsRepository channelsRepository;
    @Autowired
    private SecurityManager securityManager;

    public void updateChannelPassbookTicketTemplates(Long channelId, ChannelTemplateTicketType type, String code) {
        getAndValidateChannel(channelId);
        ChannelUpdateRequest request = new ChannelUpdateRequest();
        if (ChannelTemplateTicketType.SINGLE.equals(type)) {
            request.setPassbookTemplate(code);
        }
        channelsRepository.updateChannel(channelId, request);
    }


    public List<ChannelTicketTemplateDTO> getChannelTicketTemplates(Long channelId) {
        ChannelResponse channel = getAndValidateChannel(channelId);
        return ChannelTicketTemplatesConverter.convert(channel.getPassbookTemplate());
    }

    private ChannelResponse getAndValidateChannel(Long channelId) {
        ChannelResponse channelResponse = channelsRepository.getChannel(channelId);
        if (channelResponse == null || channelResponse.getStatus() == ChannelStatus.DELETED) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        }
        securityManager.checkEntityAccessibleWithVisibility(channelResponse.getEntityId());

        return channelResponse;
    }
}
