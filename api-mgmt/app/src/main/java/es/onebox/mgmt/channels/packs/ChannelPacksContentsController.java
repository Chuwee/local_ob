package es.onebox.mgmt.channels.packs;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.packs.dto.comelements.PackContentImageFilter;
import es.onebox.mgmt.packs.dto.comelements.PackContentImageListDTO;
import es.onebox.mgmt.packs.dto.comelements.PackContentTextFilter;
import es.onebox.mgmt.packs.dto.comelements.PackContentTextListDTO;
import es.onebox.mgmt.packs.enums.PackContentImageType;
import es.onebox.mgmt.packs.enums.PackContentTextType;
import es.onebox.mgmt.channels.packs.service.ChannelPacksContentsService;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(ChannelPacksContentsController.BASE_URI)
public class ChannelPacksContentsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/packs/{packId}/contents";

    private static final String AUDIT_COLLECTION = "CHANNEL_PACK_CONTENTS";
    private static final String AUDIT_SUBCOLLECTION_IMAGES = "IMAGES";
    private static final String AUDIT_SUBCOLLECTION_TEXTS = "TEXTS";

    private static final String CHANNEL_ID_MUST_BE_ABOVE_0 = "Channel Id must be above 0";
    private static final String PACK_ID_MUST_BE_ABOVE_0 = "Pack Id must be above 0";

    @Autowired
    private ChannelPacksContentsService packsContentsService;

    @Secured({ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_MGR})
    @GetMapping(value = "/texts")
    public ChannelContentTextListDTO<PackContentTextType> getPackContentsTexts(@PathVariable @Min(value = 1, message = CHANNEL_ID_MUST_BE_ABOVE_0) Long channelId,
                                                                               @PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                                                               @BindUsingJackson @Valid PackContentTextFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TEXTS, AuditTag.AUDIT_ACTION_SEARCH);
        return packsContentsService.getPackContentsTexts(channelId, packId, filter);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/texts", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePackContentsTexts(@PathVariable @Min(value = 1, message = CHANNEL_ID_MUST_BE_ABOVE_0) Long channelId,
                                        @PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                        @Valid @RequestBody PackContentTextListDTO contents) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TEXTS, AuditTag.AUDIT_ACTION_UPDATE);
        packsContentsService.updatePackContentTexts(channelId, packId, contents.getTexts());
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/images")
    public ChannelContentImageListDTO<PackContentImageType> getPackContentsImages(@PathVariable @Min(value = 1, message = CHANNEL_ID_MUST_BE_ABOVE_0) Long channelId,
                                                                                  @PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                                                                  @BindUsingJackson @Valid PackContentImageFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGES, AuditTag.AUDIT_ACTION_SEARCH);
        return packsContentsService.getPackContentsImages(channelId, packId, filter);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/images", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelContentsImages(@PathVariable @Min(value = 1, message = CHANNEL_ID_MUST_BE_ABOVE_0) Long channelId,
                                            @PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                            @Valid @RequestBody PackContentImageListDTO contents) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGES, AuditTag.AUDIT_ACTION_UPDATE);
        ChannelContentsUtils.validatePackContents(contents, true);
        packsContentsService.updateChannelContentImages(channelId, packId, contents.getImages());
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/images/languages/{language}/types/{type}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannelContentsImage(@PathVariable @Min(value = 1, message = CHANNEL_ID_MUST_BE_ABOVE_0) Long channelId,
                                           @PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                           @PathVariable String language, @PathVariable PackContentImageType type,
                                           @RequestParam(required = false) Integer position) {
        if (PackContentImageType.LANDSCAPE.equals(type)) {
            ChannelContentsUtils.validatePosition(position);
        }
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGES, AuditTag.AUDIT_ACTION_DELETE);
        packsContentsService.deleteChannelContentImages(channelId, packId, language, type, position);

    }
}
