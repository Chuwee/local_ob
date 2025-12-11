package es.onebox.mgmt.channels.notifications;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.notifications.converter.ChannelEmailTemplateConverter;
import es.onebox.mgmt.channels.notifications.dto.ChannelEmailTemplatesDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailTemplates;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelEmailTemplateService {

    private final ChannelsRepository channelsRepository;
    private final SecurityManager securityManager;

    @Autowired
    public ChannelEmailTemplateService(ChannelsRepository channelsRepository, SecurityManager securityManager) {
        this.channelsRepository = channelsRepository;
        this.securityManager = securityManager;
    }

    public ChannelEmailTemplatesDTO getEmailTemplates(Long channelId) {
        validateChannel(channelId);
        ChannelEmailTemplates response = channelsRepository.getChannelEmailTemplates(channelId);
        return ChannelEmailTemplateConverter.fromMs(response);
    }

    public void updateEmailTemplates(Long channelId, ChannelEmailTemplatesDTO request) {
        validateChannel(channelId);
        ChannelEmailTemplates msRequest = ChannelEmailTemplateConverter.toMs(request);
        channelsRepository.updateChannelEmailTemplates(channelId, msRequest);
    }

    private void validateChannel(Long channelId) {
        ChannelResponse channelResponse = channelsRepository.getChannel(channelId);
        if (channelResponse == null || channelResponse.getStatus() == ChannelStatus.DELETED) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        }

        if (Boolean.TRUE.equals(ChannelType.EXTERNAL.equals(channelResponse.getType()))) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }

        securityManager.checkEntityAccessible(channelResponse.getEntityId());
    }
}