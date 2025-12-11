package es.onebox.mgmt.notifications.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.mgmt.datasources.ms.notification.dto.CreateNotificationConfig;
import es.onebox.mgmt.datasources.ms.notification.dto.NotificationConfig;
import es.onebox.mgmt.datasources.ms.notification.dto.NotificationConfigs;
import es.onebox.mgmt.datasources.ms.notification.dto.SearchNotificationConfigFilter;
import es.onebox.mgmt.datasources.ms.notification.dto.UpdateNotificationConfig;
import es.onebox.mgmt.entities.dto.EventAction;
import es.onebox.mgmt.notifications.dto.CreateNotificationConfigDTO;
import es.onebox.mgmt.notifications.dto.NotificationConfigDTO;
import es.onebox.mgmt.notifications.dto.NotificationConfigsDTO;
import es.onebox.mgmt.notifications.dto.SearchNotificationConfigFilterDTO;
import es.onebox.mgmt.notifications.dto.UpdateNotificationConfigDTO;
import es.onebox.mgmt.notifications.enums.NotificationType;
import es.onebox.mgmt.notifications.enums.NotificationsScope;
import es.onebox.mgmt.notifications.enums.NotificationsStatus;
import es.onebox.mgmt.notifications.enums.NotificationsVisible;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationsConfigConverter {

    private NotificationsConfigConverter() {
    }

    public static NotificationConfigDTO fromMsNotification(NotificationConfig config) {
        if(config == null){
            return null;
        }
        NotificationConfigDTO dto = new NotificationConfigDTO();
        dto.setId(config.getDocumentId());
        dto.setScope(config.getScope());
        IdNameDTO operator = new IdNameDTO();
        operator.setId(config.getOperatorId());
        dto.setOperator(operator);
        IdNameDTO entity = new IdNameDTO();
        entity.setId(config.getEntityId());
        dto.setEntity(entity);
        if (dto.getScope().equals(NotificationsScope.CHANNEL)) {
            IdNameDTO channel = new IdNameDTO();
            channel.setId(config.getChannelId());
            dto.setChannel(channel);
        }
        dto.setVisible(config.getVisible());
        dto.setStatus(config.getStatus());
        dto.setEvents(config.getEvents().keySet().stream().flatMap(key -> {
            List<EventAction> actions = config.getEvents().get(key).stream()
                    .map(value -> EventAction.valueOf(key.name() + "_" + value))
                    .collect(Collectors.toList());
            return actions.stream();
        }).collect(Collectors.toList()));
        dto.setUrl(config.getUrl());
        dto.setApiKey(config.getApiKey());
        dto.setInternalName(config.getInternalName());

        return dto;
    }

    public static NotificationConfigsDTO fromMsNotification(NotificationConfigs configs) {
        if (configs == null) {
            return null;
        }
        NotificationConfigsDTO notificationConfigs = new NotificationConfigsDTO();
        Metadata metadata = configs.getMetadata();
        notificationConfigs.setMetadata(metadata);
        if (configs.getData() != null && !configs.getData().isEmpty()) {
            notificationConfigs.setData(configs.getData().stream().map(NotificationsConfigConverter::fromMsNotification).collect(Collectors.toList()));
        }
        return notificationConfigs;
    }

    public static CreateNotificationConfig toMsNotification(CreateNotificationConfigDTO updateDTO, Long operatorId) {
        CreateNotificationConfig config = new CreateNotificationConfig();
        if (updateDTO != null) {
            config.setScope(updateDTO.getScope() == null ? NotificationsScope.ENTITY : updateDTO.getScope());
            config.setOperatorId(operatorId);
            config.setEntityId(updateDTO.getEntityId());
            config.setChannelId(updateDTO.getChannelId());
            config.setVisible(NotificationsVisible.VISIBLE);
            config.setStatus(NotificationsStatus.ACTIVE);
            config.setEvents(updateDTO.getEvents().stream().collect(Collectors.toMap(
                    event -> NotificationType.valueOf(event.name().split("_")[0]),
                    event -> List.of(event.name().split("_")[1]),
                    (list1, list2) -> {
                        List<String> finalList = new ArrayList<>();
                        finalList.addAll(list1);
                        finalList.addAll(list2);
                        return finalList;
                    })));
            config.setUrl(updateDTO.getUrl());
            config.setInternalName(updateDTO.getInternalName());
        }
        return config;
    }

    public static UpdateNotificationConfig toMsNotification(UpdateNotificationConfigDTO updateDTO) {
        UpdateNotificationConfig config = new UpdateNotificationConfig();
        if (updateDTO != null) {
            config.setStatus(updateDTO.getStatus());
            if (updateDTO.getEvents() != null) {
                config.setEvents(updateDTO.getEvents().stream().collect(Collectors.toMap(
                        event -> NotificationType.valueOf(event.name().split("_")[0]),
                        event -> List.of(event.name().split("_")[1]),
                        (list1, list2) -> {
                            List<String> finalList = new ArrayList<>();
                            finalList.addAll(list1);
                            finalList.addAll(list2);
                            return finalList;
                        })));
            }
            config.setUrl(updateDTO.getUrl());
            config.setInternalName(updateDTO.getInternalName());
        }
        return config;
    }

    public static SearchNotificationConfigFilter toMsNotification(SearchNotificationConfigFilterDTO filterDTO) {
        SearchNotificationConfigFilter filter = new SearchNotificationConfigFilter();
        filter.setEntityId(filterDTO.getEntityId());
        filter.setChannelId(filterDTO.getChannelId());
        filter.setStatus(filterDTO.getStatus());
        filter.setScope(filterDTO.getScope());
        filter.setVisible(filterDTO.getVisible());
        filter.setOffset(filterDTO.getOffset());
        filter.setLimit(filterDTO.getLimit());
        filter.setOperatorId(filterDTO.getOperatorId());
        filter.setSort(filterDTO.getSort());

        return filter;
    }
}
