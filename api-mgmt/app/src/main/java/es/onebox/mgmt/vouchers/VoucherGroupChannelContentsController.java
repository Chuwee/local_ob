package es.onebox.mgmt.vouchers;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.vouchers.dto.VoucherChannelContentTextFilter;
import es.onebox.mgmt.vouchers.dto.VoucherGroupChannelContentTextListDTO;
import es.onebox.mgmt.vouchers.enums.VoucherChannelContentTextType;
import es.onebox.mgmt.vouchers.service.VoucherGroupsChannelContentsService;
import jakarta.validation.Valid;
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
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = VoucherGroupChannelContentsController.BASE_URI)
public class VoucherGroupChannelContentsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/voucher-groups/{voucherGroupId}/channel-contents";
    private static final String AUDIT_COLLECTION = "VOUCHER_CHANNEL_CONTENTS";

    private final VoucherGroupsChannelContentsService voucherGroupsChannelContentsService;

    @Autowired
    public VoucherGroupChannelContentsController(VoucherGroupsChannelContentsService voucherGroupsChannelContentsService) {
        this.voucherGroupsChannelContentsService = voucherGroupsChannelContentsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/texts")
    public ChannelContentTextListDTO<VoucherChannelContentTextType> getChannelContentsTexts(@PathVariable Long voucherGroupId,
                                                                                            @BindUsingJackson @Valid VoucherChannelContentTextFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return voucherGroupsChannelContentsService.getChannelContentTexts(voucherGroupId, filter);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/texts",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelContentsTexts(
            @PathVariable Long voucherGroupId,
            @Valid @RequestBody VoucherGroupChannelContentTextListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        voucherGroupsChannelContentsService.updateChannelContentTexts(voucherGroupId, contents.getTexts());
    }
}
