package es.onebox.mgmt.channels.purchasecontents;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.purchasecontents.dto.ChannelPurchaseImageContentsDTO;
import es.onebox.mgmt.channels.purchasecontents.dto.ChannelPurchaseTextsContentsDTO;
import es.onebox.mgmt.channels.purchasecontents.enums.ChannelPurchaseImageContentType;
import es.onebox.mgmt.channels.purchasecontents.enums.ChannelPurchaseTextsContentType;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelPurchaseContentsController.BASE_URI)
public class ChannelPurchaseContentsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/purchase-contents";

    private static final String AUDIT_COLLECTION = "CHANNEL_EMAIL_CONTENTS";

    private final ChannelPurchaseContentsService channelPurchaseContentsService;

    @Autowired
    public ChannelPurchaseContentsController(ChannelPurchaseContentsService channelPurchaseContentsService) {
        this.channelPurchaseContentsService = channelPurchaseContentsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/images")
    public ChannelPurchaseImageContentsDTO getEmailImage(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                         @RequestParam(value = "language", required = false) @LanguageIETF String language,
                                                         @RequestParam(value = "type", required = false) ChannelPurchaseImageContentType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.channelPurchaseContentsService.getEmailImage(channelId, language, type);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.POST, value = "/images")
    public void updateEmailImage(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                 @RequestBody @NotEmpty @Valid ChannelPurchaseImageContentsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.channelPurchaseContentsService.updateEmailImage(channelId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/images/languages/{language}/types/{type}")
    public void deleteEmailImage(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                 @PathVariable @LanguageIETF String language,
                                 @PathVariable ChannelPurchaseImageContentType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        this.channelPurchaseContentsService.deleteEmailImage(channelId, language, type);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/texts")
    public ChannelPurchaseTextsContentsDTO getEmailUrl(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                       @RequestParam(value = "language", required = false) @LanguageIETF String language,
                                                       @RequestParam(value = "type", required = false) ChannelPurchaseTextsContentType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.channelPurchaseContentsService.getEmailText(channelId, language, type);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.POST, value = "/texts")
    public void updateEmailUrl(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                               @RequestBody @NotEmpty @Valid ChannelPurchaseTextsContentsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.channelPurchaseContentsService.updateEmailTexts(channelId, body);
    }
}
