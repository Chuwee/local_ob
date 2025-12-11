package es.onebox.mgmt.channels.surcharges;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.ChannelsController;
import es.onebox.mgmt.common.surcharges.dto.SurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeListDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.config.AuditTag;
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

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = ChannelSurchargeController.BASE_URI)
public class ChannelSurchargeController {

    public static final String BASE_URI = ChannelsController.BASE_URI + "/{channelId}/surcharges";
    private static final String AUDIT_COLLECTION = "CHANNEL_SURCHARGES";

    private final ChannelSurchargeService channelSurchargeService;

    @Autowired
    public ChannelSurchargeController(ChannelSurchargeService channelSurchargeService) {
        this.channelSurchargeService = channelSurchargeService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PostMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setSurcharge(@PathVariable Long channelId, @RequestBody SurchargeListDTO surchargeListDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        channelSurchargeService.setSurcharge(channelId, surchargeListDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @GetMapping()
    public List<SurchargeDTO> getSurcharges(@PathVariable Long channelId,
                                            @RequestParam(value = "type", required = false) List<SurchargeTypeDTO> types) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return channelSurchargeService.getSurcharges(channelId, types);
    }
}
