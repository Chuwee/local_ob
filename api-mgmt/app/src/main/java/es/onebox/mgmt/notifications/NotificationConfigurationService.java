package es.onebox.mgmt.notifications;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.serializer.dto.request.Direction;
import es.onebox.core.serializer.dto.request.SortDirection;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.OperatorsRepository;
import es.onebox.mgmt.datasources.ms.notification.dto.NotificationConfigs;
import es.onebox.mgmt.datasources.ms.notification.repository.NotificationsConfigRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.notifications.converter.NotificationsConfigConverter;
import es.onebox.mgmt.notifications.dto.CreateNotificationConfigDTO;
import es.onebox.mgmt.notifications.dto.NotificationConfigDTO;
import es.onebox.mgmt.notifications.dto.NotificationConfigsDTO;
import es.onebox.mgmt.notifications.dto.SearchNotificationConfigFilterDTO;
import es.onebox.mgmt.notifications.dto.UpdateNotificationConfigDTO;
import es.onebox.mgmt.notifications.enums.NotificationsScope;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationConfigurationService {

    private final NotificationsConfigRepository notificationsConfigRepository;
    private final EntitiesRepository entitiesRepository;
    private final ChannelsRepository channelsRepository;
    private final OperatorsRepository operatorsRepository;

    @Autowired
    public NotificationConfigurationService(NotificationsConfigRepository notificationsConfigRepository,
                                            EntitiesRepository entitiesRepository,
                                            ChannelsRepository channelsRepository,
        OperatorsRepository operatorsRepository) {
        this.notificationsConfigRepository = notificationsConfigRepository;
        this.entitiesRepository = entitiesRepository;
        this.channelsRepository = channelsRepository;
        this.operatorsRepository = operatorsRepository;
    }

    public NotificationConfigDTO getNotificationConfig(String documentId) {
        NotificationConfigDTO result = NotificationsConfigConverter.fromMsNotification(notificationsConfigRepository.getNotificationConfig(documentId));
        if (result == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.WEBHOOK_CONFIG_NOT_FOUND);
        }
        validateOwnership(result);
        populateNames(result);
        return result;
    }

    public NotificationConfigsDTO searchNotificationConfigs(SearchNotificationConfigFilterDTO filter) {
        filter.setEntityId(validateFilterEntityIdAndScope(filter.getEntityId(), filter.getScope()));
        validateChannelId(filter.getScope(), filter.getEntityId(), filter.getChannelId());
        validateOperatorId(filter.getOperatorId());
        NotificationConfigs configs = notificationsConfigRepository.searchNotificationConfigs(NotificationsConfigConverter.toMsNotification(filter));
        NotificationConfigsDTO result = NotificationsConfigConverter.fromMsNotification(configs);
        if (result == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.WEBHOOK_CONFIG_NOT_FOUND);
        }
        List<NotificationConfigDTO> data = result.getData();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(config -> {
                validateOwnership(config);
                populateNames(config);
            });

            sort(filter, result);
        }
        return result;
    }

    private void sort(SearchNotificationConfigFilterDTO filter, NotificationConfigsDTO result) {
        if (filter.getSort() != null){
            String value = filter.getSort().getSortDirections().stream()
                .findFirst()
                .map(SortDirection::getValue)
                .orElse(null);

            Direction direction = filter.getSort().getSortDirections().stream()
                .findFirst()
                .map(SortDirection::getDirection)
                .orElse(null);
            if (value != null && direction != null) {
                switch (value.toLowerCase()) {
                    case "entity" -> result.setData(sortEntity(result, direction));
                    case "operator" -> result.setData(sortOperator(result, direction));
                }
            }
        }
    }

    private List<NotificationConfigDTO> sortEntity(NotificationConfigsDTO response, Direction direction) {

        switch (direction) {
            case ASC -> {
               return response.getData().stream().sorted(Comparator.comparing(object -> object.getEntity().getName().toLowerCase())).toList();
            }
            case DESC -> {
                return response.getData().stream()
                    .sorted(Comparator.comparing((NotificationConfigDTO object) -> object.getEntity().getName().toLowerCase()).reversed()).toList();
            }
            default -> {return response.getData();}
        }
    }

    private List<NotificationConfigDTO> sortOperator(NotificationConfigsDTO response, Direction direction) {

        switch (direction) {
            case ASC -> {
                return response.getData().stream()
                    .sorted(Comparator.comparing(object -> object.getOperator().getName())).toList();
            }
            case DESC -> {
                return response.getData().stream()
                    .sorted(Comparator.comparing(
                        (NotificationConfigDTO object) -> object.getOperator().getName()).reversed()
                    ).toList();
            }
            default -> {return response.getData();}
        }
    }

    public NotificationConfigDTO createNotificationConfig(CreateNotificationConfigDTO createDTO) {
        Long operatorId;
        if (SecurityUtils.isOperatorEntity()) {
            operatorId = SecurityUtils.getUserEntityId();
        } else if (SecurityUtils.hasEntityType(EntityTypes.SUPER_OPERATOR)) {
            validateOperatorIdBySuperOperator(createDTO.getOperatorId());
            operatorId = createDTO.getOperatorId();
        } else {
            operatorId = entitiesRepository.getCachedEntity(SecurityUtils.getUserEntityId()).getOperator().getId();
        }
        createDTO.setEntityId(validateEntityId(createDTO.getEntityId()));
        validateChannelId(Collections.singletonList(createDTO.getScope()), createDTO.getEntityId(), createDTO.getChannelId());
        validateInternalName(createDTO);
        if (NotificationsScope.OPERATOR.equals(createDTO.getScope())) {
            validateOperatorScopeRelation(createDTO.getOperatorId(), createDTO.getEntityId(), createDTO.getScope());
            validateUniqueOperatorConfig(createDTO.getOperatorId(), createDTO.getEntityId());
        }

        NotificationConfigDTO result = NotificationsConfigConverter.fromMsNotification(notificationsConfigRepository.createNotificationConfig(
                NotificationsConfigConverter.toMsNotification(createDTO, operatorId)));
        populateNames(result);
        return result;
    }

    private void validateOperatorScopeRelation(Long operatorId, Long entityId, NotificationsScope scope) {
        if (NotificationsScope.OPERATOR.equals(scope) && (operatorId == null || entityId == null || !operatorId.equals(entityId))) {
                throw new OneboxRestException(
                        ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "For OPERATOR scope, the entity must belong to the specified operator", null
                );
            }
    }

    private void validateUniqueOperatorConfig(Long operatorId, Long entityId) {
        SearchNotificationConfigFilterDTO filter = new SearchNotificationConfigFilterDTO();
        filter.setScope(Collections.singletonList(NotificationsScope.OPERATOR));
        filter.setOperatorId(operatorId);
        filter.setEntityId(entityId);

        NotificationConfigs configs = notificationsConfigRepository.searchNotificationConfigs(
                NotificationsConfigConverter.toMsNotification(filter)
        );
        if (configs != null && configs.getData() != null && !configs.getData().isEmpty()) {
            throw new OneboxRestException(ApiMgmtErrorCode.OPERATOR_SCOPE_WEBHOOK_ALREADY_EXISTS);
        }
    }

    public void updateNotificationConfig(String documentId, UpdateNotificationConfigDTO updateDTO) {
        NotificationConfigDTO config = this.getNotificationConfig(documentId);
        validateInternalName(updateDTO, config);
        notificationsConfigRepository.updateNotificationConfig(documentId, NotificationsConfigConverter.toMsNotification(updateDTO));
    }

    public void deleteNotificationConfig(String documentId) {
        this.getNotificationConfig(documentId);
        notificationsConfigRepository.deleteNotificationConfig(documentId);
    }

    public NotificationConfigDTO regenerateApiKey(String documentId) {
        this.getNotificationConfig(documentId);
        NotificationConfigDTO result = NotificationsConfigConverter.fromMsNotification(notificationsConfigRepository.regenerateApiKey(documentId));
        populateNames(result);
        return result;
    }

    private Long validateEntityId(Long entityId){
        if (SecurityUtils.isOperatorEntity()) {
            return isEntityIdNegative(entityId);
        } else if (SecurityUtils.hasEntityType(EntityTypes.SUPER_OPERATOR)) {

            if (entityId == null || entityId <= 0) {
                throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_ID_MANDATORY);
            }

            Entity e = entitiesRepository.getEntity(entityId);
            if (e == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_NOT_FOUND);
            }

            return entityId;

        } else {
            return SecurityUtils.getUserEntityId();
        }
    }

    private Long validateFilterEntityIdAndScope(Long entityId, List<NotificationsScope> scopes){
        if (SecurityUtils.isOperatorEntity()) {
            return isEntityIdNegative(entityId);
        } else if (SecurityUtils.hasEntityType(EntityTypes.SUPER_OPERATOR)) {
            boolean entityOptionalScope = scopes != null &&
                    (scopes.contains(NotificationsScope.SYS_ADMIN) || scopes.contains(NotificationsScope.OPERATOR));
            if (entityId == null && !entityOptionalScope) {
                throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_ID_MANDATORY);
            }
            return entityId;
        } else {
            return SecurityUtils.getUserEntityId();
        }
    }

    private void validateChannelId(List<NotificationsScope> scopes, Long entityId, Long channelId){
        if (scopes != null && scopes.contains(NotificationsScope.CHANNEL)) {
            if (channelId == null || channelId <= 0) {
                throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_ID_MANDATORY);
            }
            ChannelResponse channel = channelsRepository.getChannel(channelId);
            if (!channel.getEntityId().equals(entityId)) {
                throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_CHANNEL_INVALID_REL);
            }
        }
    }

    private void validateOperatorId(Long operatorId){
        if (!SecurityUtils.hasEntityType(EntityTypes.SUPER_OPERATOR) && operatorId != null) {
            throw new OneboxRestException(ApiMgmtErrorCode.OPERATOR_FILTER_NOT_ALLOWED);
        }

        if (operatorId != null){
            Optional.ofNullable(operatorsRepository.getOperator(operatorId))
                .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.OPERATOR_NOT_FOUND));
        }
    }

    private void validateInternalName(CreateNotificationConfigDTO config) {
        boolean requiresInternalName = NotificationsScope.SYS_ADMIN.equals(config.getScope())
                || NotificationsScope.OPERATOR.equals(config.getScope());

        if (requiresInternalName && StringUtils.isEmpty(config.getInternalName())) {
            throw new OneboxRestException(
                    ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                    "Internal Name cannot be null or empty when scope is SYS_ADMIN or OPERATOR",
                    null
            );
        } else if (!requiresInternalName && config.getInternalName() != null) {
            throw new OneboxRestException(
                    ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                    "Internal Name must be empty or null when scope is different from SYS_ADMIN or OPERATOR",
                    null
            );
        }

        if (requiresInternalName) {
            SearchNotificationConfigFilterDTO filter = new SearchNotificationConfigFilterDTO();
            filter.setScope(Collections.singletonList(config.getScope()));
            filter.setOperatorId(config.getOperatorId());
            filter.setEntityId(config.getEntityId());
            validateRepeatedInternalName(filter, config.getInternalName());
        }
    }

    private void validateRepeatedInternalName(SearchNotificationConfigFilterDTO filter, String internalName) {

        NotificationConfigsDTO configs = searchNotificationConfigs(filter);

        if (configs != null && configs.getData() != null &&
                configs.getData().stream().anyMatch(conf -> internalName.equals(conf.getInternalName()))) {
                throw new OneboxRestException(ApiMgmtErrorCode.NAME_CONFLICT, "There is already a config with this internal name in the entity and operator", null);
            }

    }

    private void validateInternalName(UpdateNotificationConfigDTO updateRequest, NotificationConfigDTO config) {
        boolean requiresInternalName = NotificationsScope.SYS_ADMIN.equals(config.getScope())
                || NotificationsScope.OPERATOR.equals(config.getScope());

        if (!requiresInternalName  && updateRequest.getInternalName() != null){
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Internal Name must be empty or null when scope is different from SYS_ADMIN or OPERATOR", null);
        }

        if (requiresInternalName && updateRequest.getInternalName() != null) {
            SearchNotificationConfigFilterDTO filter = new SearchNotificationConfigFilterDTO();
            filter.setScope(Collections.singletonList(config.getScope()));
            filter.setOperatorId(config.getOperator().getId());
            filter.setEntityId(config.getEntity().getId());

            validateRepeatedInternalName(filter, updateRequest.getInternalName());
        }
    }

    private void validateOwnership(NotificationConfigDTO result) {
        if (SecurityUtils.isOperatorEntity()) {
            Entity e = entitiesRepository.getCachedEntity(result.getEntity().getId());
            if (!e.getOperator().getId().equals(SecurityUtils.getUserEntityId())) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_OPERATOR_ID);
            }
        } else if(!SecurityUtils.hasEntityType(EntityTypes.SUPER_OPERATOR) &&
                !result.getEntity().getId().equals(SecurityUtils.getUserEntityId())) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_ENTITY_ID);
            }

    }

    private void validateOperatorIdBySuperOperator(Long operatorId) {

        if (operatorId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.OPERATOR_MANDATORY);
        }

        Optional.ofNullable(operatorsRepository.getOperator(operatorId))
            .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.OPERATOR_NOT_FOUND));
    }

    private void populateNames(NotificationConfigDTO result) {
        Entity e = entitiesRepository.getCachedEntity(result.getEntity().getId());
        result.getEntity().setName(e.getName());
        Entity o = entitiesRepository.getCachedEntity(result.getOperator().getId());
        result.getOperator().setName(o.getName());
        if (result.getScope().equals(NotificationsScope.CHANNEL)) {
            ChannelResponse c = channelsRepository.getChannel(result.getChannel().getId());
            result.getChannel().setName(c.getName());
        }
    }
    private Long isEntityIdNegative(Long entityId) {
        if (entityId == null || entityId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_ID_MANDATORY);
        }
        if (entityId.equals(SecurityUtils.getUserEntityId())) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_OPERATOR_ID);
        }
        Entity e = entitiesRepository.getCachedEntity(entityId);
        if (!e.getOperator().getId().equals(SecurityUtils.getUserEntityId())) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_ENTITY_ID);
        }
        return entityId;
    }

}
