package es.onebox.mgmt.users.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.users.dto.ratelimit.UserRateLimitConfigDTO;
import es.onebox.mgmt.users.service.UserRateLimitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(UserRateLimitController.BASE_URI)
public class UserRateLimitController {

    private static final String AUDIT_COLLECTION = "USERS";
    private static final String AUDIT_SUBCOLLECTION_RATE_LIMIT = "RATE_LIMIT";
    public static final String BASE_URI = ApiConfig.BASE_URL + "/users/{userId}";


    private final UserRateLimitService service;

    public UserRateLimitController(UserRateLimitService service) {
        this.service = service;
    }

    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR})
    @GetMapping("/rate-limit")
    @ResponseStatus(HttpStatus.OK)
    public UserRateLimitConfigDTO searchRateLimit(@PathVariable @Valid Long userId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RATE_LIMIT,
            AuditTag.AUDIT_ACTION_GET);
        return service.searchRateLimit(userId);
    }

    @Secured({ROLE_SYS_MGR})
    @PostMapping(value = "/rate-limit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void upsertRateLimit(@PathVariable @Valid Long userId,
        @Valid @RequestBody final UserRateLimitConfigDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RATE_LIMIT,
            AuditTag.AUDIT_ACTION_CREATE);
        service.upsertRateLimit(userId, body);
    }

}
