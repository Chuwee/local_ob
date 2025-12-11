package es.onebox.mgmt.customdomains.customersdomain.domaincustomers;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.customdomains.common.dto.DomainSettingsDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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

@RestController
@Validated
@RequestMapping(DomainCustomersSettingsController.BASE_URI)
public class DomainCustomersSettingsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/customers-domain-settings";

    private static final String AUDIT_COLLECTION = "ENTITY_CUSTOMERS_DOMAIN_SETTINGS";

    private final DomainCustomersSettingsService domainCustomersSettingsService;

    @Autowired
    public DomainCustomersSettingsController(DomainCustomersSettingsService domainCustomersSettingsService) {
        this.domainCustomersSettingsService = domainCustomersSettingsService;
    }

    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR})
    @GetMapping
    public DomainSettingsDTO get(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return domainCustomersSettingsService.get(entityId);
    }

    @Secured({ROLE_SYS_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void upsert(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId, @Valid @RequestBody DomainSettingsDTO body) {
        domainCustomersSettingsService.upsert(entityId, body);
    }

    @Secured({ROLE_SYS_MGR})
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        domainCustomersSettingsService.disable(entityId);
    }
}
