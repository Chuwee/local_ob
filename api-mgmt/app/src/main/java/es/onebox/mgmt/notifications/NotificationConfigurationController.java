package es.onebox.mgmt.notifications;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.notifications.dto.CreateNotificationConfigDTO;
import es.onebox.mgmt.notifications.dto.NotificationConfigDTO;
import es.onebox.mgmt.notifications.dto.NotificationConfigsDTO;
import es.onebox.mgmt.notifications.dto.SearchNotificationConfigFilterDTO;
import es.onebox.mgmt.notifications.dto.UpdateNotificationConfigDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;

@RestController
@RequestMapping(value = NotificationConfigurationController.BASE_URI)
public class NotificationConfigurationController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/webhooks";
    private static final String AUDIT_COLLECTION = "ENTITY_NOTIFICATION_SYSTEM";

    private final NotificationConfigurationService notificationConfigurationService;

    @Autowired
    public NotificationConfigurationController(NotificationConfigurationService entityNotificationConfigurationService) {
        this.notificationConfigurationService = entityNotificationConfigurationService;
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR, ROLE_SYS_ANS, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{documentId}")
    public NotificationConfigDTO getNotificationConfig(@PathVariable String documentId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return notificationConfigurationService.getNotificationConfig(documentId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR, ROLE_SYS_ANS, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public NotificationConfigsDTO getNotificationConfigs(@BindUsingJackson @Valid SearchNotificationConfigFilterDTO filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return notificationConfigurationService.searchNotificationConfigs(filter);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.POST)
    public NotificationConfigDTO createNotificationConfig(@Valid @RequestBody CreateNotificationConfigDTO updateDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return notificationConfigurationService.createNotificationConfig(updateDTO);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{documentId}")
    public ResponseEntity<Serializable> updateNotificationConfig(@PathVariable String documentId,
                                                                 @Valid @RequestBody UpdateNotificationConfigDTO updateDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        notificationConfigurationService.updateNotificationConfig(documentId, updateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{documentId}")
    public ResponseEntity<Serializable> deleteNotificationConfig(@PathVariable String documentId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        notificationConfigurationService.deleteNotificationConfig(documentId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{documentId}/apikey/regenerate")
    public NotificationConfigDTO regenerateApiKey(@PathVariable String documentId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        return notificationConfigurationService.regenerateApiKey(documentId);
    }
}
