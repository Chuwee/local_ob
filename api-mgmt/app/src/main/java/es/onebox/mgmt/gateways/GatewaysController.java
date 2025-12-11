package es.onebox.mgmt.gateways;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.gateways.dto.GatewayConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = ApiConfig.BASE_URL + "/gateways")
public class GatewaysController {

    private static final String AUDIT_COLLECTION = "GATEWAYS";

    @Autowired
    private GatewaysService gatewaysService;

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/{gatewaySid}")
    public GatewayConfigDTO getGatewayConfig(@PathVariable String gatewaySid) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        if (CommonUtils.isBlank(gatewaySid)) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_GATEWAY_SID);
        }

        return gatewaysService.gatewayConfig(gatewaySid);
    }

    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public List<GatewayConfigDTO> getGateways() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return gatewaysService.getGateways();
    }
}
