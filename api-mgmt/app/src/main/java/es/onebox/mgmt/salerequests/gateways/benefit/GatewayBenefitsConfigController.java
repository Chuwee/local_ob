package es.onebox.mgmt.salerequests.gateways.benefit;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.GatewayBenefitsConfigDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.GatewayBenefitsConfigRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(value = GatewayBenefitsConfigController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class GatewayBenefitsConfigController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/catalog-sale-requests/{saleRequestId}/gateways";
    private static final String AUDIT_COLLECTION = "GATEWAY_BENEFITS";

    private final GatewayBenefitsConfigService gatewayBenefitsConfigService;

    @Autowired
    public GatewayBenefitsConfigController(GatewayBenefitsConfigService gatewayBenefitsConfigService) {
        this.gatewayBenefitsConfigService = gatewayBenefitsConfigService;
    }

    @GetMapping(value = "/{gatewaySid}/configs/{confSid}/benefits")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public GatewayBenefitsConfigDTO getGatewayBenefitsConfig(
            @PathVariable Long saleRequestId,
            @PathVariable String gatewaySid,
            @PathVariable String confSid) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return gatewayBenefitsConfigService.getGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid);
    }

    @GetMapping(value = "/benefits")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public List<GatewayBenefitsConfigDTO> getListGatewayBenefitsConfigs(
            @PathVariable Long saleRequestId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return gatewayBenefitsConfigService.getListGatewayBenefitsConfigs(saleRequestId);
    }

    @PostMapping(value = "/{gatewaySid}/configs/{confSid}/benefits", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    public GatewayBenefitsConfigDTO createGatewayBenefitsConfig(
            @PathVariable Long saleRequestId,
            @PathVariable String gatewaySid,
            @PathVariable String confSid,
            @Valid @RequestBody GatewayBenefitsConfigRequest request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return gatewayBenefitsConfigService.createGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid, request);
    }

    @PutMapping(value = "/{gatewaySid}/configs/{confSid}/benefits", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    public GatewayBenefitsConfigDTO updateGatewayBenefitsConfig(
            @PathVariable Long saleRequestId,
            @PathVariable String gatewaySid,
            @PathVariable String confSid,
            @Valid @RequestBody GatewayBenefitsConfigRequest request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        return gatewayBenefitsConfigService.updateGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid, request);
    }

    @DeleteMapping(value = "/{gatewaySid}/configs/{confSid}/benefits")
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGatewayBenefitsConfig(
            @PathVariable Long saleRequestId,
            @PathVariable String gatewaySid,
            @PathVariable String confSid) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        gatewayBenefitsConfigService.deleteGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid);
    }
}
