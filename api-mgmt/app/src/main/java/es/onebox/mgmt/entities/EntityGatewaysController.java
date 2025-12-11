package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.dto.EntityGatewayConfigDTO;
import es.onebox.mgmt.entities.dto.GatewayConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_CNL_TAQ;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = EntityGatewaysController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class EntityGatewaysController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/gateways";

    private static final String AUDIT_COLLECTION = "GATEWAYS";
    @Autowired
    private EntityGatewaysService entityGatewaysService;

    @Secured({ROLE_OPR_MGR, ROLE_CNL_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "")
    public List<GatewayConfigDTO> getAvailableGateways(@PathVariable Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        ConverterUtils.checkField(entityId, "entityId");
        return entityGatewaysService.getAvailableGateways(entityId);
    }

    @Secured({ROLE_CNL_TAQ})
    @GetMapping("/config")
    public List<EntityGatewayConfigDTO> getEntityGatewaysConfig(@PathVariable Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        ConverterUtils.checkField(entityId, "entityId");
        return entityGatewaysService.getEntityGatewaysConfig(entityId);
    }
}
