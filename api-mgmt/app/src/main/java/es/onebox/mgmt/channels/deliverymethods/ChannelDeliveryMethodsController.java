package es.onebox.mgmt.channels.deliverymethods;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.deliverymethods.dto.ChannelDeliveryMethodsDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.ChannelDeliveryMethodsUpdateDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelDeliveryMethodsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelDeliveryMethodsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/delivery";

    private static final String AUDIT_COLLECTION = "CHANNEL_DELIVERY_METHODS";
    private final ChannelDeliveryMethodsService channelDeliveryMethodsService;

    @Autowired
    public ChannelDeliveryMethodsController(ChannelDeliveryMethodsService channelDeliveryMethodsService) {
        this.channelDeliveryMethodsService = channelDeliveryMethodsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public ChannelDeliveryMethodsDTO getDeliveryMethods(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return channelDeliveryMethodsService.getById(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDeliveryMethods(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                      @RequestBody @Valid ChannelDeliveryMethodsUpdateDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        channelDeliveryMethodsService.updateByChannelId(channelId, request);
    }

}
