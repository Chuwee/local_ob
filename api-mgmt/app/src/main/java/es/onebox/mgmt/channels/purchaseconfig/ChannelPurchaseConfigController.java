package es.onebox.mgmt.channels.purchaseconfig;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigDTO;
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
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelPurchaseConfigController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelPurchaseConfigController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/purchase-config";

    private static final String AUDIT_COLLECTION = "CHANNEL_PURCHASE_CONFIG";

    private final ChannelPurchaseConfigService service;

    @Autowired
    public ChannelPurchaseConfigController(ChannelPurchaseConfigService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping
    public ChannelPurchaseConfigDTO getPurchaseConfig(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return service.getPurchaseConfig(channelId);
    }


    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePurchaseConfig(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                     @RequestBody @Valid ChannelPurchaseConfigDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updatePurchaseConfig(channelId, body);
    }
}
