package es.onebox.mgmt.entities.customertypes;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.customertypes.dto.CreateCustomerTypeDTO;
import es.onebox.mgmt.entities.customertypes.dto.CustomerTypeDTO;
import es.onebox.mgmt.entities.customertypes.dto.CustomerTypesDTO;
import es.onebox.mgmt.entities.customertypes.dto.UpdateCustomerTypeDTO;
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
import static es.onebox.core.security.Roles.Codes.ROLE_CRM_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = CustomerTypesController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerTypesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/customer-types";

    private static final String AUDIT_COLLECTION = "CUSTOMER_TYPES";

    private final CustomerTypesService service;

    @Autowired
    public CustomerTypesController(CustomerTypesService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CRM_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public CustomerTypesDTO getCustomerTypes(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return service.getCustomerTypes(entityId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{customerTypeId}")
    public CustomerTypeDTO get(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                               @PathVariable @Min(value = 1, message = "customerTypeId must be above 0") Long customerTypeId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getCustomerType(entityId, customerTypeId);
    }

    @Secured({ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO create(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                        @Valid @RequestBody CreateCustomerTypeDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return service.createCustomerType(entityId, request);
    }

    @Secured({ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.PUT, value = "/{customerTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                       @PathVariable @Min(value = 1, message = "customerTypeId must be above 0") Long customerTypeId,
                       @Valid @RequestBody UpdateCustomerTypeDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateCustomerType(entityId, customerTypeId, request);
    }

    @Secured({ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{customerTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                       @PathVariable @Min(value = 1, message = "customerTypeId must be above 0") Long customerTypeId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        service.deleteCustomerType(entityId, customerTypeId);
    }
}
