package es.onebox.mgmt.channels.notifications;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.notifications.converter.ChannelEmailServerConverter;
import es.onebox.mgmt.channels.notifications.dto.ChannelEmailServerDTO;
import es.onebox.mgmt.channels.notifications.dto.ChannelEmailTestDTO;
import es.onebox.mgmt.channels.notifications.dto.EmailServerConfiguration;
import es.onebox.mgmt.channels.notifications.enums.EmailServerType;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailServer;
import es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailTemplates;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelEmailServerType;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.delivery.dto.ChannelEmailTest;
import es.onebox.mgmt.datasources.ms.delivery.repositories.DeliveryEmailRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ChannelEmailServerService {

    private final ChannelsRepository channelsRepository;
    private final SecurityManager securityManager;
    private final DeliveryEmailRepository deliveryEmailRepository;

    @Autowired
    public ChannelEmailServerService(final ChannelsRepository channelsRepository,
                                     final SecurityManager securityManager,
                                     final DeliveryEmailRepository deliveryEmailRepository) {
        this.channelsRepository = channelsRepository;
        this.securityManager = securityManager;
        this.deliveryEmailRepository = deliveryEmailRepository;
    }

    public ChannelEmailServerDTO getConfiguration(Long channelId) {
        validateChannel(channelId);
        ChannelEmailServer channelEmailServer = channelsRepository.getChannelEmailServer(channelId);
        return ChannelEmailServerConverter.toDto(channelEmailServer);
    }

    public void updateConfiguration(final Long channelId, final ChannelEmailServerDTO request) {
        validate(request);
        validateChannel(channelId);
        ChannelEmailServer updateRequest = ChannelEmailServerConverter.prepareUpdateRequest(request,
                channelsRepository.getChannelEmailServer(channelId));
        validateConfiguration(channelId, updateRequest);
        channelsRepository.updateChannelEmailServer(channelId, updateRequest);
    }

    public void test(Long channelId, ChannelEmailTestDTO body) {
        validateChannel(channelId);
        validate(body);
        ChannelEmailTest testRequest = ChannelEmailServerConverter.toMs(body, channelsRepository.getChannelEmailServer(channelId));
        fillFromEmail(channelId, testRequest);
        if (testRequest.getFromEmail() == null && testRequest.getUser() != null && testRequest.getUser().contains("@")) {
            testRequest.setFromEmail(testRequest.getUser());
        }
        deliveryEmailRepository.sendTestEmail(testRequest);
    }

    private void validateConfiguration(Long channelId, ChannelEmailServer request) {
        if (ChannelEmailServerType.OTHER.equals(request.getType())) {
            ChannelEmailTest testRequest = ChannelEmailServerConverter.toMs(request.getConfiguration());
            fillFromEmail(channelId, testRequest);
            if (testRequest.getFromEmail() == null) {
                if (testRequest.getUser() != null && testRequest.getUser().contains("@")) {
                    testRequest.setFromEmail(testRequest.getUser());
                } else {
                    testRequest.setFromEmail(null);
                }
            }
            deliveryEmailRepository.sendTestEmail(testRequest);
        } else if (ChannelEmailServerType.ONEBOX.equals(request.getType())) {
            //TODO temporarilly disabled
//            ChannelEmailTest testRequest = new ChannelEmailTest();
//            fillFromEmail(channelId, testRequest);
//            deliveryEmailRepository.sendTestEmail(testRequest);
        }
    }

    private void fillFromEmail(Long channelId, ChannelEmailTest testRequest) {
        ChannelEmailTemplates channelEmailTemplates = channelsRepository.getChannelEmailTemplates(channelId);
        if (CollectionUtils.isNotEmpty(channelEmailTemplates)) {
            testRequest.setFromEmail(channelEmailTemplates.get(0).getFrom());
        }
    }

    private void validate(final ChannelEmailServerDTO request) {
        if (Objects.isNull(request) || Objects.isNull(request.getType())) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.EMAIL_SERVER_TYPE_MANDATORY);
        }
        if (EmailServerType.OTHER.equals(request.getType())) {
            validate(request.getConfiguration());
        }
    }

    private void validate(final EmailServerConfiguration configuration) {
        if (Objects.isNull(configuration)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.EMAIL_SERVER_CONFIGURATION_MANDATORY);
        }
        if (StringUtils.isBlank(configuration.getServer())) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.EMAIL_SERVER_SERVER_MANDATORY);
        }
        if (Objects.isNull(configuration.getPort()) || configuration.getPort() < 0) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.EMAIL_SERVER_PORT_MANDATORY);
        }
        if (Objects.isNull(configuration.getSecurity())) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.EMAIL_SERVER_SECURITY_MANDATORY);
        }
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
