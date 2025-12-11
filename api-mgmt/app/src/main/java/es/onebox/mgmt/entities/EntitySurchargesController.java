package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.surcharges.dto.EntitySurchargesDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_ACTION_CREATE;
import static es.onebox.mgmt.config.AuditTag.AUDIT_ACTION_SEARCH;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = EntitySurchargesController.BASE_URI)
public class EntitySurchargesController {

    public static final String BASE_URI = EntitiesController.BASE_URI + "/{entityId}/surcharges";
    private static final String AUDIT_COLLECTION = "ENTITY_SURCHARGES";

    private final EntitySurchargesService entitiesSurchargeService;

    @Autowired
    public EntitySurchargesController(EntitySurchargesService entitiesSurchargeService) {
        this.entitiesSurchargeService = entitiesSurchargeService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @GetMapping
    public EntitySurchargesDTO getSurcharges(@PathVariable Long entityId,
                                             @RequestParam(value= "type", required = false) List<SurchargeTypeDTO> surchargeTypes,
                                             @RequestParam(value = "currency_code", required = false) String currencyCode) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_ACTION_SEARCH);

        return entitiesSurchargeService.getSurcharges(entityId, surchargeTypes, currencyCode);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setSurcharges(@PathVariable Long entityId,
                              @RequestBody EntitySurchargesDTO surcharges) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_ACTION_CREATE);

        entitiesSurchargeService.setSurcharges(entityId, surcharges);
    }
}
