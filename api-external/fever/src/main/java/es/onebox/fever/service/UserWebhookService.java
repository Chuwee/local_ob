package es.onebox.fever.service;

import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.webhook.dto.fever.UserDetailDTO;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserWebhookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserWebhookService.class);

    private final UsersRepository usersRepository;
    private final EntitiesRepository entitiesRepository;

    public UserWebhookService(UsersRepository usersRepository, EntitiesRepository entitiesRepository) {
        this.usersRepository = usersRepository;
        this.entitiesRepository = entitiesRepository;
    }

    public WebhookFeverDTO sendUserFvZoneData(WebhookFeverDTO webhookFever) {

        Long userId = Long.valueOf(webhookFever.getNotificationMessage().getId());

        User user = usersRepository.getById(userId);

        if (user == null) {
            webhookFever.setAllowSend(Boolean.FALSE);
            return webhookFever;
        }

        EntityDTO entity = entitiesRepository.getById(user.getEntityId());

        validateIfUserNeedsNotification(entity, webhookFever);

        if (Boolean.FALSE.equals(webhookFever.getAllowSend())) {
            return webhookFever;
        }

        UserDetailDTO userDetail = new UserDetailDTO();

        userDetail.setFvId(StringUtils.isNumeric(user.getExternalReference()) ? Integer.parseInt(user.getExternalReference()) : null);
        userDetail.setEntityId(user.getEntityId().toString());
        userDetail.setFvPartnerId(Integer.valueOf(entity.getExternalReference()));
        userDetail.setName(user.getName());
        userDetail.setEmail(user.getEmail());

        webhookFever.getFeverMessage().setUserDetail(userDetail);
        webhookFever.getFeverMessage().setId(String.valueOf(userId));

        LOGGER.info("[FEVER WEBHOOK] User with id: {} was received for sync with fever.", userId);

        return webhookFever;
    }

    public void validateIfUserNeedsNotification(EntityDTO entity, WebhookFeverDTO webhookFever) {
        if (StringUtils.isEmpty(webhookFever.getHeaders().getHeader("ob-action"))) {
            webhookFever.setAllowSend(Boolean.FALSE);
            return;
        }

        boolean ret = entity != null && entity.getExternalReference() != null &&
                Boolean.TRUE.equals(entity.getAllowFeverZone());

        webhookFever.setAllowSend(ret);
    }

}
