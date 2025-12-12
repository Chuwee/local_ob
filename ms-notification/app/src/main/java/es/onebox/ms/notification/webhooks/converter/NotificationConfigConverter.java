package es.onebox.ms.notification.webhooks.converter;

import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.ms.notification.webhooks.dto.CreateNotificationConfigDTO;
import es.onebox.ms.notification.webhooks.dto.NotificationConfig;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigDTO;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigs;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigsDTO;
import es.onebox.ms.notification.webhooks.dto.SearchNotificationConfigFilterDTO;
import es.onebox.ms.notification.webhooks.dto.UpdateNotificationConfigDTO;
import org.apache.commons.collections.CollectionUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

public class NotificationConfigConverter {

    private NotificationConfigConverter() {
    }

    public static NotificationConfigDTO convert(NotificationConfig config) {
        if (config == null) {
            return null;
        }
        NotificationConfigDTO out = new NotificationConfigDTO();
        out.setDocumentId(config.getDocumentId());
        out.setScope(config.getScope());
        out.setStatus(config.getStatus());
        out.setVisible(config.getVisible());
        out.setOperatorId(config.getOperatorId());
        out.setEntityId(config.getEntityId());
        out.setChannelId(config.getChannelId());
        out.setEvents(config.getEvents());
        out.setUrl(config.getUrl());
        out.setApiKey(config.getApiKey());
        out.setInternalName(config.getInternalName());
        out.setCreatedAt(config.getCreatedAt());
        return out;
    }

    public static NotificationConfigsDTO convert(SearchNotificationConfigFilterDTO filter, NotificationConfigs configs) {
        if (configs == null) {
            return null;
        }
        NotificationConfigsDTO out = new NotificationConfigsDTO();
        Metadata metadata = new Metadata();
        metadata.setTotal(configs.getTotalElements());
        metadata.setLimit(filter.getLimit());
        metadata.setOffset(filter.getOffset() == null ? Long.valueOf(0) : filter.getOffset());
        out.setMetadata(metadata);
        if (CollectionUtils.isNotEmpty(configs.getConfigs())) {
            out.setData(configs.getConfigs().stream().map(NotificationConfigConverter::convert).collect(Collectors.toList()));
        }
        return out;
    }

    public static NotificationConfig convert(String id, CreateNotificationConfigDTO createDTO, String apiKey) {
        if (createDTO == null) {
            return null;
        }
        NotificationConfig config = new NotificationConfig();
        config.setDocumentId(id);
        config.setScope(createDTO.getScope());
        config.setStatus(createDTO.getStatus());
        config.setOperatorId(createDTO.getOperatorId());
        config.setEntityId(createDTO.getEntityId());
        config.setChannelId(createDTO.getChannelId());
        config.setEvents(createDTO.getEvents());
        config.setVisible(createDTO.getVisible());
        config.setUrl(createDTO.getUrl());
        config.setApiKey(apiKey);
        config.setInternalName(createDTO.getInternalName());
        config.setCreatedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        return config;
    }

    public static NotificationConfig convert(UpdateNotificationConfigDTO newUpdateDTO, NotificationConfig config) {
        if (newUpdateDTO == null) {
            return config;
        }
        if (newUpdateDTO.getStatus() != null) {
            config.setStatus(newUpdateDTO.getStatus());
        }
        if (newUpdateDTO.getUrl() != null) {
            config.setUrl(newUpdateDTO.getUrl());
        }
        if (newUpdateDTO.getEvents() != null) {
            config.setEvents(newUpdateDTO.getEvents());
        }
        if (newUpdateDTO.getInternalName() != null) {
            config.setInternalName(newUpdateDTO.getInternalName());
        }
        return config;
    }
}
