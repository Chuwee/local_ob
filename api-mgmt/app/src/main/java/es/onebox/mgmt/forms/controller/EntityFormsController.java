package es.onebox.mgmt.forms.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.forms.enums.EntityFormType;
import es.onebox.mgmt.forms.dto.FormFieldDTO;
import es.onebox.mgmt.forms.dto.UpdateFormDTO;
import es.onebox.mgmt.forms.service.EntityFormsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = EntityFormsController.BASE_URI)
public class EntityFormsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/forms";

    private static final String AUDIT_COLLECTION = "ENTITY_FORMS";

    private final EntityFormsService service;

    @Autowired
    public EntityFormsController(EntityFormsService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping("/customer")
    public List<List<FormFieldDTO>> getCustomerForm(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getEntityForm(entityId, EntityFormType.CUSTOMER);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping("/customer-sign-in")
    public List<List<FormFieldDTO>> getCustomerSigninForm(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getEntityForm(entityId, EntityFormType.CUSTOMERSIGNIN);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping("/payout")
    public List<List<FormFieldDTO>> getPayoutForm(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getEntityForm(entityId, EntityFormType.PAYOUT);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping("/admincustomer")
    public List<List<FormFieldDTO>> getAdminCustomerForm(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getEntityForm(entityId, EntityFormType.ADMINCUSTOMER);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping("/admincustomer-sign-in")
    public List<List<FormFieldDTO>> getAdminCustomerSigninForm(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getEntityForm(entityId, EntityFormType.ADMINCUSTOMERSIGNIN);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping("/customer")
    public void updateCustomerForm(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                                   @RequestBody @Valid @NotNull UpdateFormDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateEntityForm(entityId, body, EntityFormType.CUSTOMER);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping("/admincustomer")
    public void updateAdminCustomerForm(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                                      @RequestBody @Valid @NotNull UpdateFormDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateEntityForm(entityId, body, EntityFormType.ADMINCUSTOMER);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping("/customer-sign-in")
    public void updateCustomerSigninForm(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                                   @RequestBody @Valid @NotNull UpdateFormDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateEntityForm(entityId, body, EntityFormType.CUSTOMERSIGNIN);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping("/admincustomer-sign-in")
    public void updateAdminCustomerSigninForm(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                                         @RequestBody @Valid @NotNull UpdateFormDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateEntityForm(entityId, body, EntityFormType.ADMINCUSTOMERSIGNIN);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping("/payout")
    public void updatePayoutForm(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                                   @RequestBody @Valid @NotNull UpdateFormDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateEntityForm(entityId, body, EntityFormType.PAYOUT);
    }
}
