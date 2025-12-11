package es.onebox.mgmt.users.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.users.dto.ChangeSelfPwdRequestDTO;
import es.onebox.mgmt.users.dto.MFARequestDTO;
import es.onebox.mgmt.users.dto.MFAResponseDTO;
import es.onebox.mgmt.users.dto.NotificationDTO;
import es.onebox.mgmt.users.dto.NotificationsDTO;
import es.onebox.mgmt.users.dto.UpdateAuthUserRequestDTO;
import es.onebox.mgmt.users.dto.UserAuthUrlsDTO;
import es.onebox.mgmt.users.dto.UserSecretDTO;
import es.onebox.mgmt.users.dto.UserSelfDTO;
import es.onebox.mgmt.users.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = AuthUserController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthUserController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/users/myself";

    private static final String AUDIT_COLLECTION = "USERS";
    private static final String AUDIT_SUBCOLLECTION_PASS = "PASSWORD";
    private static final String AUDIT_SUBCOLLECTION_NOTIFICATIONS = "NOTIFICATIONS";
    private static final String AUDIT_SUBCOLLECTION_MFA = "MFA";

    private final UsersService usersService;

    @Autowired
    public AuthUserController(UsersService usersService) {
        this.usersService = usersService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public UserSelfDTO getAuthUser() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return usersService.getAuthUser();
    }

    @RequestMapping(method = RequestMethod.PUT,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAuthUser(@RequestBody UpdateAuthUserRequestDTO userSelf) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        usersService.updateAuthUser(userSelf);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/apikey/refresh")
    @ResponseStatus(HttpStatus.OK)
    public UserSecretDTO refreshSelfApiKey() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASS, AuditTag.AUDIT_ACTION_REFRESH);
        return usersService.refreshApiKey(SecurityUtils.getUserId());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setSelfPassword(@RequestBody @Valid ChangeSelfPwdRequestDTO req) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASS, AuditTag.AUDIT_ACTION_UPDATE);
        usersService.setSelfPassword(req.oldPassword(), req.password());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/notifications")
    public List<NotificationDTO> getSelfNotifications() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_NOTIFICATIONS, AuditTag.AUDIT_ACTION_GET);

        return usersService.getUserNotifications(SecurityUtils.getUserId());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/notifications")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSelfNotifications(@RequestBody NotificationsDTO notifications) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        for (NotificationDTO notification : notifications) {
            ConverterUtils.checkField(notification.getType(), "type");
            ConverterUtils.checkField(notification.getEnable(), "enable");
        }
        usersService.setUserNotifications(SecurityUtils.getUserId(), notifications);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/mstrUrls")
    public UserAuthUrlsDTO getMstrUrls(@RequestParam(value = "impersonated_user_id", required = false) Long impersonatedUserId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return usersService.getMstrUrls(SecurityUtils.getUserId(), impersonatedUserId);
    }

    @PostMapping("/mfa-activation-send")
    @ResponseStatus(HttpStatus.OK)
    public MFAResponseDTO sendMFAActivationEmail(@RequestBody @Valid MFARequestDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_MFA, AuditTag.AUDIT_ACTION_UPDATE);
        return usersService.sendMFAActivationEmail(request);
    }

    @PostMapping("/mfa-activation-confirm")
    @ResponseStatus(HttpStatus.OK)
    public MFAResponseDTO validateAndActivateMFA(@RequestBody @Valid MFARequestDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_MFA, AuditTag.AUDIT_ACTION_UPDATE);
        return usersService.validateAndActivateMFA(request);
    }
}
