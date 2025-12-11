package es.onebox.mgmt.events.eventchannel;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.EventChannelContentImageFilter;
import es.onebox.mgmt.events.dto.EventChannelContentImageListDTO;
import es.onebox.mgmt.events.dto.EventChannelContentTextFilter;
import es.onebox.mgmt.events.dto.EventChannelContentTextListDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelContentTextType;
import es.onebox.mgmt.events.enums.EventChannelContentImageType;
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
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(EventChannelContentsController.BASE_URI)
public class EventChannelContentsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/channel-contents";

    private static final String AUDIT_COLLECTION = "EVENT_CHANNEL_CONTENTS";
    private static final String AUDIT_SUBCOLLECTION_IMAGES = "IMAGES";
    private static final String AUDIT_SUBCOLLECTION_TEXTS = "TEXTS";

    private static final String EVENT_ID_MUST_BE_ABOVE_0 = "Event Id must be above 0";

    @Autowired
    private EventChannelContentsService eventsChannelContentService;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_MGR})
    @GetMapping(value = "/texts")
    public ChannelContentTextListDTO<EventChannelContentTextType> getChannelContentsTexts(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @BindUsingJackson @Valid EventChannelContentTextFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TEXTS, AuditTag.AUDIT_ACTION_SEARCH);
        return eventsChannelContentService.getChannelContentTexts(eventId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/texts", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelContentsTexts(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @Valid @RequestBody EventChannelContentTextListDTO contents) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TEXTS, AuditTag.AUDIT_ACTION_UPDATE);
        eventsChannelContentService.updateChannelContentTexts(eventId, contents.getTexts());
    }

    @Secured({ROLE_EVN_MGR, ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/images")
    public ChannelContentImageListDTO<EventChannelContentImageType> getChannelContentsImages(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @BindUsingJackson @Valid EventChannelContentImageFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGES, AuditTag.AUDIT_ACTION_SEARCH);
        return eventsChannelContentService.getChannelContentImages(eventId, filter);
    }

    @Secured({ ROLE_EVN_MGR, ROLE_OPR_MGR })
    @PostMapping(value = "/images", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelContentsImages(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @Valid @RequestBody EventChannelContentImageListDTO contents) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGES, AuditTag.AUDIT_ACTION_UPDATE);
        ChannelContentsUtils.validateEventContents(contents, true);
        eventsChannelContentService.updateChannelContentImages(eventId, contents.getImages());
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/images/languages/{language}/types/{type}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannelContentsImage(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable String language, @PathVariable EventChannelContentImageType type,
            @RequestParam(required = false) Integer position) {
        if (EventChannelContentImageType.LANDSCAPE.equals(type)) {
            ChannelContentsUtils.validatePosition(position);
        }
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGES, AuditTag.AUDIT_ACTION_DELETE);
        eventsChannelContentService.deleteChannelContentImages(eventId, language, type, position);

    }
}
