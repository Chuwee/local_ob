package es.onebox.mgmt.entities.customertypetriggers;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = CustomerTypeTriggersController.BASE_URI)
public class CustomerTypeTriggersController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/masterdata/customer-type-triggers";

    private static final String AUDIT_COLLECTION = "CUSTOMER_TYPE_TRIGGERS";

    private final CustomerTypeTriggersService service;

    @Autowired
    public CustomerTypeTriggersController(CustomerTypeTriggersService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public List<IdNameDTO> getCustomerTriggers(){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return service.getCustomerTypeTriggers();
    }

}
