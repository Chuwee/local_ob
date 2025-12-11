package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.dto.CookieSettingsDTO;
import es.onebox.mgmt.entities.dto.CookieSettingsUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(
        value = CookieSettingsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class CookieSettingsController {

    public static final String BASE_URI = EntitiesController.BASE_URI + "/{entityId}/cookies";

    private final CookieSettingsService cookiesService;

    private static final String AUDIT_COLLECTION = "ENTITIES";
    private static final String AUDIT_SUBCOLLECTION_COOKIES = "COOKIES";

    @Autowired
    public CookieSettingsController(CookieSettingsService cookiesService){
        this.cookiesService = cookiesService;
    }

    @GetMapping
    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @ResponseStatus(HttpStatus.OK)
    public CookieSettingsDTO getCookieSettings(@PathVariable Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_COOKIES, AuditTag.AUDIT_ACTION_GET);
        return cookiesService.getCookieSettings(entityId);
    }

    @PutMapping
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCookieSettings(@PathVariable Long entityId,
                                     @RequestBody @Valid @NotNull CookieSettingsUpdateDTO requestBody) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_COOKIES, AuditTag.AUDIT_ACTION_UPDATE);
        cookiesService.updateCookieSettings(entityId, requestBody);
    }
}
