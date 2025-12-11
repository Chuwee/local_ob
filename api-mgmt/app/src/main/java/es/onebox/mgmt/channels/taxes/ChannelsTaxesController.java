package es.onebox.mgmt.channels.taxes;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.taxes.dto.ChannelSurchargesTaxesDTO;
import es.onebox.mgmt.channels.taxes.dto.ChannelSurchargesTaxesUpdateDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelsTaxesController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelsTaxesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/surcharges-taxes";

    private static final String AUDIT_COLLECTION = "CHANNELS_SURCHARGES_TAXES";

    private final ChannelsTaxesService channelsTaxesService;

    @Autowired
    public ChannelsTaxesController(ChannelsTaxesService channelsTaxesService) {
        this.channelsTaxesService = channelsTaxesService;
    }


    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR, ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public ChannelSurchargesTaxesDTO getChannelsSurchargesTaxes(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return channelsTaxesService.getChannelsSurchargesTaxes(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void updateChannelsSurchargesTaxes(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                              @Valid @RequestBody ChannelSurchargesTaxesUpdateDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        channelsTaxesService.updateChannelsSurchargesTaxes(channelId, body);
    }

}