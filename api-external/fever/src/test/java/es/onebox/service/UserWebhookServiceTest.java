package es.onebox.service;

import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.webhook.dto.fever.FeverMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.NotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.fever.service.UserWebhookService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

public class UserWebhookServiceTest {

    private static final String USER_ID = "1";
    private static final Long ENTITY_ID = 1L;


    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserWebhookService userWebhookService;


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void sendUserData_create_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage("CREATE");
        webhookFever.getNotificationMessage().setId(USER_ID);

        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setExternalReference("123");
        entity.setAllowFeverZone(Boolean.TRUE);

        User user = new User();
        user.setEntityId(ENTITY_ID);
        user.setEmail("test@test.com");
        user.setName("test");
        user.setExternalReference(null);

        when(usersRepository.getById(Long.valueOf(USER_ID))).thenReturn(user);
        when(entitiesRepository.getById(ENTITY_ID)).thenReturn(entity);

        WebhookFeverDTO response = userWebhookService.sendUserFvZoneData(webhookFever);
        Assertions.assertEquals(USER_ID, response.getFeverMessage().getId());
        Assertions.assertEquals(user.getEmail(), response.getFeverMessage().getUserDetail().getEmail());
        Assertions.assertEquals(user.getName(), response.getFeverMessage().getUserDetail().getName());
        Assertions.assertEquals(Integer.valueOf(entity.getExternalReference()), response.getFeverMessage().getUserDetail().getFvPartnerId());
    }

    @Test
    public void sendUserData_reactivate_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage("REACTIVATE");
        webhookFever.getNotificationMessage().setId(USER_ID);

        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setExternalReference("123");
        entity.setAllowFeverZone(Boolean.TRUE);

        User user = new User();
        user.setEntityId(ENTITY_ID);
        user.setEmail("test@test.com");
        user.setName("test");
        user.setExternalReference("123");

        when(usersRepository.getById(Long.valueOf(USER_ID))).thenReturn(user);
        when(entitiesRepository.getById(ENTITY_ID)).thenReturn(entity);

        WebhookFeverDTO response = userWebhookService.sendUserFvZoneData(webhookFever);
        Assertions.assertEquals(USER_ID, response.getFeverMessage().getId());
        Assertions.assertEquals(user.getEmail(), response.getFeverMessage().getUserDetail().getEmail());
        Assertions.assertEquals(user.getName(), response.getFeverMessage().getUserDetail().getName());
        Assertions.assertEquals(Integer.valueOf(entity.getExternalReference()), response.getFeverMessage().getUserDetail().getFvPartnerId());
        Assertions.assertEquals(Integer.valueOf(entity.getExternalReference()), response.getFeverMessage().getUserDetail().getFvId());
    }

    @Test
    public void sendUserData_noUser_doNothing() {
        WebhookFeverDTO webhookFever = generateWebhookMessage("REACTIVATE");
        webhookFever.getNotificationMessage().setId(USER_ID);

        when(usersRepository.getById(Long.valueOf(USER_ID))).thenReturn(null);

        WebhookFeverDTO response = userWebhookService.sendUserFvZoneData(webhookFever);
        assertFalse(response.getAllowSend());
    }

    @Test
    public void sendUserData_noEntity_doNothing() {
        WebhookFeverDTO webhookFever = generateWebhookMessage("REACTIVATE");
        webhookFever.getNotificationMessage().setId(USER_ID);

        User user = new User();
        user.setEntityId(ENTITY_ID);
        user.setEmail("test@test.com");
        user.setName("test");
        user.setExternalReference("123");

        when(usersRepository.getById(Long.valueOf(USER_ID))).thenReturn(user);
        when(entitiesRepository.getById(ENTITY_ID)).thenReturn(null);

        WebhookFeverDTO response = userWebhookService.sendUserFvZoneData(webhookFever);
        assertFalse(response.getAllowSend());
    }

    @Test
    public void sendUserData_noEntityExternalReference_doNothing() {
        WebhookFeverDTO webhookFever = generateWebhookMessage("REACTIVATE");
        webhookFever.getNotificationMessage().setId(USER_ID);

        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setAllowFeverZone(Boolean.TRUE);

        User user = new User();
        user.setEntityId(ENTITY_ID);
        user.setEmail("test@test.com");
        user.setName("test");
        user.setExternalReference("123");

        when(usersRepository.getById(Long.valueOf(USER_ID))).thenReturn(user);
        when(entitiesRepository.getById(ENTITY_ID)).thenReturn(entity);

        WebhookFeverDTO response = userWebhookService.sendUserFvZoneData(webhookFever);
        assertFalse(response.getAllowSend());
    }

    @Test
    public void sendUserData_noEntityAllowFvZone_doNothing() {
        WebhookFeverDTO webhookFever = generateWebhookMessage("REACTIVATE");
        webhookFever.getNotificationMessage().setId(USER_ID);

        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setExternalReference("123");
        entity.setAllowFeverZone(Boolean.FALSE);

        User user = new User();
        user.setEntityId(ENTITY_ID);
        user.setEmail("test@test.com");
        user.setName("test");
        user.setExternalReference("123");

        when(usersRepository.getById(Long.valueOf(USER_ID))).thenReturn(user);
        when(entitiesRepository.getById(ENTITY_ID)).thenReturn(entity);

        WebhookFeverDTO response = userWebhookService.sendUserFvZoneData(webhookFever);
        assertFalse(response.getAllowSend());
    }

    private WebhookFeverDTO generateWebhookMessage(String headerAction) {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("ob-action")).thenReturn(headerAction);
        NotificationMessageDTO notificationMessage = new NotificationMessageDTO();
        FeverMessageDTO message = new FeverMessageDTO();
        return new WebhookFeverDTO(notificationMessage, req, message);
    }
}
