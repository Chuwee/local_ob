package es.onebox.mgmt.channels.commissions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.ChannelsController;
import es.onebox.mgmt.channels.commissions.dto.CommissionDTO;
import es.onebox.mgmt.channels.commissions.dto.CommissionListDTO;
import es.onebox.mgmt.channels.commissions.dto.CommissionTypeDTO;
import es.onebox.mgmt.config.AuditTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = ChannelCommissionController.BASE_URI)
public class ChannelCommissionController {

    public static final String BASE_URI = ChannelsController.BASE_URI + "/{channelId}/commissions";
    private static final String AUDIT_COLLECTION = "CHANNEL_COMMISSIONS";

    private final ChannelCommissionService channelCommissionService;

    @Autowired
    public ChannelCommissionController(ChannelCommissionService channelCommissionService) {
        this.channelCommissionService = channelCommissionService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public List<CommissionDTO> getCommissions(@PathVariable Long channelId,
                                              @RequestParam(value = "type", required = false) List<CommissionTypeDTO> types) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return channelCommissionService.getCommissions(channelId, types);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setCommission(@PathVariable Long channelId, @RequestBody CommissionListDTO commissionListDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        channelCommissionService.setCommissions(channelId, commissionListDTO);
    }
}
