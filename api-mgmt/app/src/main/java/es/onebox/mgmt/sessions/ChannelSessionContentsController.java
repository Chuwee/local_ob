package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.ChannelEventContentImageFilter;
import es.onebox.mgmt.events.dto.ChannelEventContentImageUpdateRequest;
import es.onebox.mgmt.events.enums.ChannelEventContentImageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_INT;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(value = ChannelSessionContentsController.BASE_URI)
public class ChannelSessionContentsController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/channels/{channelId}/channel-contents";

    private static final String AUDIT_COLLECTION = "SESSION_CHANNEL_CONTENTS";
    private static final String AUDIT_SUBCOLLECTION_IMAGES = "IMAGES";
    private static final String EVENT_ID_MUST_BE_ABOVE_0 = "Event Id must be above 0";
    private static final String SESSION_ID_MUST_BE_ABOVE_0 = "Session Id must be above 0";
    private static final String CHANNEL_ID_MUST_BE_ABOVE_0 = "Channel Id must be above 0";


    private final ChannelSessionContentsService channelSessionContentsService;

    @Autowired
    public ChannelSessionContentsController(ChannelSessionContentsService channelSessionContentsService) {
        this.channelSessionContentsService = channelSessionContentsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_INT})
    @GetMapping(value = "/images")
    public ChannelContentImageListDTO<ChannelEventContentImageType> getChannelSessionImages(@PathVariable Long eventId,
                                                                                            @PathVariable Long sessionId,
                                                                                            @PathVariable Long channelId,
                                                                                            @BindUsingJackson @Valid ChannelEventContentImageFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGES, AuditTag.AUDIT_ACTION_SEARCH);
        return channelSessionContentsService.getChannelSessionImages(eventId, sessionId, channelId, filter);
    }

    @Secured({ ROLE_EVN_MGR, ROLE_OPR_MGR })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/images")
    public void updateChannelSessionImages(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                         @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                         @PathVariable @Min(value = 1, message = CHANNEL_ID_MUST_BE_ABOVE_0) Long channelId,
                                         @Valid @RequestBody ChannelEventContentImageUpdateRequest request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGES, AuditTag.AUDIT_ACTION_UPDATE);
        channelSessionContentsService.updateChannelSessionImages(eventId, sessionId, channelId, request);
    }

    @Secured({ ROLE_EVN_MGR, ROLE_OPR_MGR })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/images/languages/{language}/types/{type}")
    public void deleteChannelSessionImage(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                        @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                        @PathVariable @Min(value = 1, message = CHANNEL_ID_MUST_BE_ABOVE_0) Long channelId,
                                        @PathVariable String language, @PathVariable ChannelEventContentImageType type,
                                        @RequestParam(required = false) Integer position) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGES, AuditTag.AUDIT_ACTION_DELETE);
        channelSessionContentsService.deleteChannelSessionImage(eventId, sessionId, channelId, language, type, position);
    }

    @Secured({ ROLE_EVN_MGR, ROLE_OPR_MGR })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/images")
    public void deleteChannelSessionImages(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                           @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                           @PathVariable @Min(value = 1, message = CHANNEL_ID_MUST_BE_ABOVE_0) Long channelId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGES, AuditTag.AUDIT_ACTION_DELETE);
        channelSessionContentsService.deleteChannelSessionImages(eventId, sessionId, channelId);
    }
}
