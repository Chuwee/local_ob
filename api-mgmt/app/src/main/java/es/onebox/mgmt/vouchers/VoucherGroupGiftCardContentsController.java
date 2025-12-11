package es.onebox.mgmt.vouchers;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.vouchers.dto.VoucherGiftCardContentImageFilter;
import es.onebox.mgmt.vouchers.dto.VoucherGiftCardContentImageListDTO;
import es.onebox.mgmt.vouchers.dto.VoucherGiftCardContentTextFilter;
import es.onebox.mgmt.vouchers.dto.VoucherGroupGiftCardContentTextListDTO;
import es.onebox.mgmt.vouchers.enums.VoucherGiftCardContentImageType;
import es.onebox.mgmt.vouchers.enums.VoucherGiftCardContentTextType;
import es.onebox.mgmt.vouchers.service.VoucherGroupGiftCardContentsService;
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
@RequestMapping(value = VoucherGroupGiftCardContentsController.BASE_URI)
public class VoucherGroupGiftCardContentsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/voucher-groups/{voucherGroupId}/gift-card-contents";

    private static final String AUDIT_COLLECTION = "VOUCHER_GIFT_CARD_CONTENTS";

    private final VoucherGroupGiftCardContentsService voucherGroupGiftCardContentsService;

    @Autowired
    public VoucherGroupGiftCardContentsController(VoucherGroupGiftCardContentsService voucherGroupGiftCardContentsService) {
        this.voucherGroupGiftCardContentsService = voucherGroupGiftCardContentsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/texts")
    public ChannelContentTextListDTO<VoucherGiftCardContentTextType> getChannelContentsTexts(@PathVariable Long voucherGroupId,
                                                                                            @BindUsingJackson @Valid VoucherGiftCardContentTextFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return voucherGroupGiftCardContentsService.getChannelContentTexts(voucherGroupId, filter);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/texts",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelContentsTexts(
            @PathVariable Long voucherGroupId,
            @Valid @RequestBody VoucherGroupGiftCardContentTextListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        voucherGroupGiftCardContentsService.updateChannelContentTexts(voucherGroupId, contents.getTexts());
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/images")
    public ChannelContentImageListDTO<VoucherGiftCardContentImageType> getChannelContentsImages(
            @PathVariable Long voucherGroupId,
            @BindUsingJackson @Valid VoucherGiftCardContentImageFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return voucherGroupGiftCardContentsService.getChannelContentImages(voucherGroupId, filter);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/images",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelContentsImages(
            @PathVariable Long voucherGroupId,
            @Valid @RequestBody VoucherGiftCardContentImageListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        ChannelContentsUtils.validateVoucherContents(contents);
        voucherGroupGiftCardContentsService.updateChannelContentImages(voucherGroupId, contents.getImages());
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/images/languages/{language}/types/{type}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannelContentsImage(@PathVariable Long voucherGroupId,
                                                                   @PathVariable String language,
                                                                   @PathVariable VoucherGiftCardContentImageType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        voucherGroupGiftCardContentsService.deleteChannelContentImages(voucherGroupId, language, type);
    }
}
