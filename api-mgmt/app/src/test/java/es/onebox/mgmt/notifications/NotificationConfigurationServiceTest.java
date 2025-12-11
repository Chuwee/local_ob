package es.onebox.mgmt.notifications;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.OperatorsRepository;
import es.onebox.mgmt.datasources.ms.notification.dto.NotificationConfig;
import es.onebox.mgmt.datasources.ms.notification.dto.NotificationConfigs;
import es.onebox.mgmt.datasources.ms.notification.repository.NotificationsConfigRepository;
import es.onebox.mgmt.entities.dto.EventAction;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.notifications.dto.UpdateNotificationConfigDTO;
import es.onebox.mgmt.notifications.enums.NotificationType;
import es.onebox.mgmt.notifications.enums.NotificationsScope;
import es.onebox.mgmt.security.SecurityUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationConfigurationServiceTest {

    private static final String DOCUMENT_ID = "1";
    private static final Long ENTITY_ID = 1L;
    private static final Long OPERATOR_ID = 2L;

    @Mock
    private NotificationsConfigRepository notificationsConfigRepository;
    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private ChannelsRepository channelsRepository;
    @Mock
    private OperatorsRepository operatorsRepository;

    private MockedStatic<SecurityUtils> securityUtils;

    @InjectMocks 
    private NotificationConfigurationService notificationConfigurationService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        this.securityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    public void clean() {
        this.securityUtils.close();
    }

    @Test
    public void updateNotificationConfig_ok() {

        NotificationConfig config = generateNotificationConfig();

        Entity operator = new Entity();
        operator.setId(OPERATOR_ID);

        Entity entity = new Entity();
        entity.setOperator(operator);

        UpdateNotificationConfigDTO requestConfig = new UpdateNotificationConfigDTO();


        when(notificationsConfigRepository.getNotificationConfig(DOCUMENT_ID)).thenReturn(config);
        when(entitiesRepository.getCachedEntity(ENTITY_ID)).thenReturn(entity);
        when(entitiesRepository.getCachedEntity(OPERATOR_ID)).thenReturn(operator);
        when(SecurityUtils.isOperatorEntity()).thenReturn(Boolean.TRUE);
        when(SecurityUtils.getUserEntityId()).thenReturn(OPERATOR_ID);

        notificationConfigurationService.updateNotificationConfig(DOCUMENT_ID, requestConfig);
        verify(notificationsConfigRepository, times(1)).updateNotificationConfig(any(), any());
    }

    @Test
    public void updateNotificationConfig_withInternalName_NoSysAdmin_ko() {

        NotificationConfig config = generateNotificationConfig();
        config.setScope(NotificationsScope.ENTITY);

        Entity operator = new Entity();
        operator.setId(OPERATOR_ID);

        Entity entity = new Entity();
        entity.setOperator(operator);

        UpdateNotificationConfigDTO requestConfig = new UpdateNotificationConfigDTO();
        requestConfig.setInternalName("test");

        when(notificationsConfigRepository.getNotificationConfig(DOCUMENT_ID)).thenReturn(config);
        when(entitiesRepository.getCachedEntity(ENTITY_ID)).thenReturn(entity);
        when(entitiesRepository.getCachedEntity(OPERATOR_ID)).thenReturn(operator);
        when(SecurityUtils.isOperatorEntity()).thenReturn(Boolean.TRUE);
        when(SecurityUtils.getUserEntityId()).thenReturn(OPERATOR_ID);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () ->
                notificationConfigurationService.updateNotificationConfig(DOCUMENT_ID, requestConfig));
        assertEquals(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER.toString(), ex.getErrorCode());
        verify(notificationsConfigRepository, times(0)).updateNotificationConfig(any(), any());
    }

    @Test
    public void updateNotificationConfig_withInternalName_SysAdmin_ok() {

        NotificationConfig config = generateNotificationConfig();
        config.setScope(NotificationsScope.SYS_ADMIN);

        Entity operator = new Entity();
        operator.setId(OPERATOR_ID);

        Entity entity = new Entity();
        entity.setOperator(operator);

        UpdateNotificationConfigDTO requestConfig = new UpdateNotificationConfigDTO();
        requestConfig.setInternalName("test");

        NotificationConfigs notificationConfigs = Mockito.mock(NotificationConfigs.class);
        when(notificationConfigs.getData()).thenReturn(List.of(config));

        when(notificationsConfigRepository.getNotificationConfig(DOCUMENT_ID)).thenReturn(config);
        when(notificationsConfigRepository.searchNotificationConfigs(any())).thenReturn(notificationConfigs);
        when(operatorsRepository.getOperator(OPERATOR_ID)).thenReturn(new Operator());
        when(entitiesRepository.getCachedEntity(ENTITY_ID)).thenReturn(entity);
        when(entitiesRepository.getCachedEntity(OPERATOR_ID)).thenReturn(operator);
        when(SecurityUtils.isOperatorEntity()).thenReturn(Boolean.TRUE);
        when(SecurityUtils.getUserEntityId()).thenReturn(OPERATOR_ID);
        when(SecurityUtils.hasEntityType(any())).thenReturn(Boolean.TRUE);

        notificationConfigurationService.updateNotificationConfig(DOCUMENT_ID, requestConfig);
        verify(notificationsConfigRepository, times(1)).updateNotificationConfig(any(), any());
    }

    private static NotificationConfig generateNotificationConfig() {
        Map<NotificationType, List<String>> events = new HashMap<>();
        events.put(NotificationType.ENTITY_FVZONE, List.of("UPDATE"));

        NotificationConfig config = new NotificationConfig();
        config.setEntityId(ENTITY_ID);
        config.setDocumentId(DOCUMENT_ID);
        config.setOperatorId(OPERATOR_ID);
        config.setScope(NotificationsScope.ENTITY);
        config.setEvents(events);
        return config;
    }
}
