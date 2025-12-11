package es.onebox.mgmt.loyaltypoints.entities.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.loyaltypoints.entities.dto.LoyaltyPointsConfigDTO;
import es.onebox.mgmt.loyaltypoints.entities.dto.UpdateLoyaltyPointsConfigDTO;
import es.onebox.mgmt.loyaltypoints.entities.service.EntityLoyaltyPointsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
import org.springframework.web.bind.annotation.PostMapping;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = EntityLoyaltyPointsController.BASE_URI)
public class EntityLoyaltyPointsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/loyalty-program";

    private static final String AUDIT_COLLECTION = "ENTITY_LOYALTY_POINTS";

    private final EntityLoyaltyPointsService service;

    @Autowired
    public EntityLoyaltyPointsController(EntityLoyaltyPointsService service) {
        this.service = service;
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @GetMapping("/config")
    public LoyaltyPointsConfigDTO getLoyaltyPoints(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getLoyaltyPoints(entityId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    @PutMapping("/config")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLoyaltyPoints(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                                    @Valid @RequestBody UpdateLoyaltyPointsConfigDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateLoyaltyPoints(entityId, body);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @PostMapping("/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetLoyaltyPoints(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.resetLoyaltyPoints(entityId);
    }
}