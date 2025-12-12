package es.onebox.fever.service;

import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EntityValidationService {

    private final ChannelRepository channelRepository;

    @Autowired
    public EntityValidationService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void validateAllowedEntities(WebhookFeverDTO webhookFever) {
        Long channelId = webhookFever.getNotificationMessage().getChannelId();
        ChannelDTO channelFromMS = channelRepository.getChannel(channelId);

        if (webhookFever.getAllowedEntitiesFileData().getEntityId().equals(channelFromMS.getEntityId())) {
            webhookFever.setAllowSend(true);
            return;
        }

        boolean isAllowed = webhookFever.getAllowedEntitiesFileData().getAllowedEntities().stream()
                .anyMatch(entity -> entity.equals(channelFromMS.getEntityId()));

        if (isAllowed && WhitelabelType.EXTERNAL.equals(channelFromMS.getWhitelabelType())) {
            webhookFever.setAllowSend(true);
            return;
        }

        webhookFever.setAllowSend(false);
    }
}
