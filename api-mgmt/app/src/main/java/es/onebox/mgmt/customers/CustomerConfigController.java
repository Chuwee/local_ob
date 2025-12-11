package es.onebox.mgmt.customers;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.customers.dto.CustomerConfigDTO;
import es.onebox.mgmt.customers.services.CustomerConfigService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = CustomerConfigController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerConfigController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/customer-config";

    private static final String AUDIT_COLLECTION = "CUSTOMER_CONFIG";

    private final CustomerConfigService service;

    @Autowired
    public CustomerConfigController(CustomerConfigService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public CustomerConfigDTO get(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getCustomerConfig(entityId);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                       @Valid @RequestBody CustomerConfigDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateCustomerConfig(entityId, request);
    }
}
