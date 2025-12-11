package es.onebox.mgmt.channels.members;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.members.service.MembersPricesService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = MembersPricesController.BASE_URI)
public class MembersPricesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}";

    private static final String AUDIT_COLLECTION = "CHANNEL_MEMBERS_PRICES";

    private final MembersPricesService membersPricesService;

    @Autowired
    public MembersPricesController(MembersPricesService membersPricesService) {
        this.membersPricesService = membersPricesService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @PutMapping(value = "/members-batch-prices")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void runMembersBatchPrices(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        membersPricesService.runMembersBatchPrices(channelId);
    }

}
