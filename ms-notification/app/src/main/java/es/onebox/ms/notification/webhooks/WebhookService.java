package es.onebox.ms.notification.webhooks;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.ms.notification.common.utils.GeneratorUtils;
import es.onebox.ms.notification.datasources.ms.channel.dto.ChannelExternalToolsDTO;
import es.onebox.ms.notification.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.ms.notification.exception.MsNotificationErrorCode;
import es.onebox.ms.notification.webhooks.converter.NotificationConfigConverter;
import es.onebox.ms.notification.webhooks.dao.NotificationConfigDao;
import es.onebox.ms.notification.webhooks.dto.CreateNotificationConfigDTO;
import es.onebox.ms.notification.webhooks.dto.NotificationConfig;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigDTO;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigsDTO;
import es.onebox.ms.notification.webhooks.dto.SearchNotificationConfigFilterDTO;
import es.onebox.ms.notification.webhooks.dto.UpdateNotificationConfigDTO;
import es.onebox.ms.notification.webhooks.validator.NotificationActionsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class WebhookService {

    private final NotificationConfigDao notificationConfigDao;
    private final ChannelRepository channelRepository;

    @Autowired
    public WebhookService(NotificationConfigDao notificationConfigDao, ChannelRepository channelRepository) {
        this.notificationConfigDao = notificationConfigDao;
        this.channelRepository = channelRepository;
    }

    public NotificationConfigDTO getNotificationConfig(String documentId) {
        return NotificationConfigConverter.convert(notificationConfigDao.get(documentId));
    }

    @Cached(key = "getNotificationConfigs", expires = 5, timeUnit = TimeUnit.MINUTES)
    public List<NotificationConfigDTO> getNotificationConfigs(@CachedArg Long entityId) {
        return notificationConfigDao.advancedGet(entityId).stream().map(NotificationConfigConverter::convert).collect(Collectors.toList());
    }

    public NotificationConfigsDTO getNotificationConfigs(SearchNotificationConfigFilterDTO filter) {
        return NotificationConfigConverter.convert(filter, notificationConfigDao.advancedGet(filter));
    }

    public NotificationConfigDTO createNotificationConfig(CreateNotificationConfigDTO createDTO) {
        NotificationActionsValidator.validateActions(createDTO.getEvents());
        String id;
        do {
            id = GeneratorUtils.generateUUID();
        } while (notificationConfigDao.get(id) != null);
        notificationConfigDao.upsert(NotificationConfigConverter.convert(id, createDTO, GeneratorUtils.generateApiKey()));
        return NotificationConfigConverter.convert(notificationConfigDao.get(id));
    }

    public void updateNotificationConfig(String documentId, UpdateNotificationConfigDTO updateDTO) {
        NotificationConfig configDTO = notificationConfigDao.get(documentId);
        if (configDTO == null) {
            throw OneboxRestException.builder(MsNotificationErrorCode.ENTITY_CONFIG_NOT_FOUND).build();
        }
        NotificationActionsValidator.validateActions(updateDTO.getEvents());
        notificationConfigDao.upsert(NotificationConfigConverter.convert(updateDTO, configDTO));
    }

    public void deleteNotificationConfig(String documentId) {
        NotificationConfig configDTO = notificationConfigDao.get(documentId);
        if (configDTO == null) {
            throw OneboxRestException.builder(MsNotificationErrorCode.ENTITY_CONFIG_NOT_FOUND).build();
        }
        notificationConfigDao.remove(documentId);
    }

    public NotificationConfigDTO regenerateApiKey(String documentId) {
        NotificationConfig configDTO = notificationConfigDao.get(documentId);
        if (configDTO == null) {
            throw OneboxRestException.builder(MsNotificationErrorCode.ENTITY_CONFIG_NOT_FOUND).build();
        }
        configDTO.setApiKey(GeneratorUtils.generateApiKey());
        notificationConfigDao.upsert(configDTO);
        return NotificationConfigConverter.convert(configDTO);
    }

    public ChannelExternalToolsDTO getChannelExternalTool(Long channelId) {
        if (channelId == null) {
            return null;
        } else {
            return channelRepository.getChannelExternalTools(channelId);
        }
    }
}
