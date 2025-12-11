package es.onebox.mgmt.channels.authvendors;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.authvendors.dto.ChannelAuthVendorDTO;
import es.onebox.mgmt.channels.authvendors.dto.ChannelAuthVendorUserDataDTO;
import es.onebox.mgmt.channels.authvendors.enums.ChannelAuthVendorsType;
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
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelAuthVendorsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelAuthVendorsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/auth-vendors";

    private static final String AUDIT_COLLECTION = "CHANNEL_AUTH_VENDORS";

    private final ChannelAuthVendorsService service;

    @Autowired
    public ChannelAuthVendorsController(ChannelAuthVendorsService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/user-data")
    public ChannelAuthVendorUserDataDTO getAuthVendorUserData(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return (ChannelAuthVendorUserDataDTO) service.getAuthVendor(channelId, ChannelAuthVendorsType.USER_DATA);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/sso")
    public ChannelAuthVendorDTO getAuthVendorSso(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return service.getAuthVendor(channelId, ChannelAuthVendorsType.SSO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/user-data")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAuthVendorUserData(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                     @RequestBody @Valid ChannelAuthVendorUserDataDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateAuthVendor(channelId, ChannelAuthVendorsType.USER_DATA, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/sso")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAuthVendorSso(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                         @RequestBody @Valid ChannelAuthVendorDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateAuthVendor(channelId, ChannelAuthVendorsType.SSO, body);
    }
}

