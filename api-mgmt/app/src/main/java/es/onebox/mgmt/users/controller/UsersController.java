package es.onebox.mgmt.users.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.users.dto.CreateUserRequestDTO;
import es.onebox.mgmt.users.dto.NotificationDTO;
import es.onebox.mgmt.users.dto.NotificationsDTO;
import es.onebox.mgmt.users.dto.SearchUsersResponse;
import es.onebox.mgmt.users.dto.UpdateUserRequestDTO;
import es.onebox.mgmt.users.dto.UpdateVisibilityDTO;
import es.onebox.mgmt.users.dto.UserResponseDTO;
import es.onebox.mgmt.users.dto.UserSearchFilter;
import es.onebox.mgmt.users.dto.UserSecretDTO;
import es.onebox.mgmt.users.service.UsersService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = UsersController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/users";

    private static final String AUDIT_COLLECTION = "USERS";
    private static final String AUDIT_SUBCOLLECTION_PASS = "PASSWORD";
    private static final String AUDIT_SUBCOLLECTION_NOTIFICATIONS = "NOTIFICATIONS";

    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_SYS_ANS, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    public UserResponseDTO getUser(@PathVariable @Min(value = 1, message = "userId must be above 0") Long userId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return usersService.getUser(userId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_SYS_ANS, ROLE_SYS_MGR})
    @GetMapping
    public SearchUsersResponse getUsers(@BindUsingJackson @Valid UserSearchFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return usersService.search(filter);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserSecretDTO createUser(@RequestBody @Valid CreateUserRequestDTO user) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        ConverterUtils.checkField(user.getEntityId(), "entity_id");
        ConverterUtils.checkField(user.getUsername(), "username");
        ConverterUtils.checkField(user.getName(), "name");

        //Send email by default
        Boolean sendEmail = user.getSendEmail() == null || user.getSendEmail();

        return usersService.create(user, sendEmail);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable Long userId, @RequestBody @Valid UpdateUserRequestDTO user) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        if (user.getId() != null && !user.getId().equals(userId)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "userId is different between pathVariable and requestBody", null);
        }
        usersService.update(userId, user);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        usersService.delete(userId);
    }


    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/{userId}/apikey/refresh")
    @ResponseStatus(HttpStatus.OK)
    public UserSecretDTO refreshApiKey(@PathVariable final Long userId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASS, AuditTag.AUDIT_ACTION_REFRESH);
        return usersService.refreshApiKey(userId);
    }


    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/notifications")
    public List<NotificationDTO> getUserNotifications(@PathVariable Long userId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_NOTIFICATIONS, AuditTag.AUDIT_ACTION_GET);

        return usersService.getUserNotifications(userId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}/notifications")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setUserNotifications(@PathVariable Long userId, @RequestBody NotificationsDTO notifications) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_NOTIFICATIONS, AuditTag.AUDIT_ACTION_UPDATE);

        for (NotificationDTO notification : notifications) {
            ConverterUtils.checkField(notification.getType(), "type");
            ConverterUtils.checkField(notification.getEnable(), "enable");
        }

        usersService.setUserNotifications(userId, notifications);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}/visibility", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserVisibility(@PathVariable Long userId, @RequestBody @Valid UpdateVisibilityDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        usersService.updateUserVisibility(userId, request);
    }
}