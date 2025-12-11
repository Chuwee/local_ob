package es.onebox.mgmt.customdomains.channeldomain.domainconfig;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.customdomains.channeldomain.domainconfig.dto.DomainConfigDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = DomainConfigController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class DomainConfigController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/domains-config";

    private static final String AUDIT_COLLECTION = "DOMAINS_CONFIG";

    private final DomainConfigService service;

    @Autowired
    public DomainConfigController(DomainConfigService service) {
        this.service = service;
    }

    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR})
    @GetMapping(value = "/{domain}")
    public DomainConfigDTO getDomainConfig(@PathVariable(required = true) String domain) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getDomainConfig(domain);
    }

    @Secured(ROLE_SYS_MGR)
    @PutMapping(value = "/{domain}")
    public void putDomainConfig(@PathVariable(required = true) String domain, @Valid @RequestBody DomainConfigDTO in) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.putDomainConfig(domain, in);
    }
}
