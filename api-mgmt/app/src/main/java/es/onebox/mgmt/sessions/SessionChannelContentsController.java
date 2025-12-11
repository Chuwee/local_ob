package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.channelcontents.SessionChannelContentImageType;
import es.onebox.mgmt.common.channelcontents.SessionChannelContentTextType;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.EventChannelContentImageFilter;
import es.onebox.mgmt.sessions.dto.SessionChannelContentImageListBulkDTO;
import es.onebox.mgmt.sessions.dto.SessionChannelContentImageListDTO;
import es.onebox.mgmt.sessions.dto.SessionChannelContentTextListBulkDTO;
import es.onebox.mgmt.sessions.dto.SessionChannelContentTextListDTO;
import es.onebox.mgmt.sessions.dto.SessionChannelContentsTextFilter;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = ApiConfig.BASE_URL
        + "/events/{eventId}/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
public class SessionChannelContentsController {

    private static final String EVENT_ID_MUST_BE_ABOVE_0 = "Event Id must be above 0";
    private static final String SESSION_ID_MUST_BE_ABOVE_0 = "Sessiond Id must be above 0";
    private static final String AUDIT_COLLECTION = "SESSION_CHANNEL_CONTENTS";


    private final SessionChannelContentsService sessionChannelContentsService;

    @Autowired
    public SessionChannelContentsController(SessionChannelContentsService sessionsService) {
        this.sessionChannelContentsService = sessionsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{sessionId}/channel-contents/texts")
    public ChannelContentTextListDTO<SessionChannelContentTextType> getChannelContentsTexts(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId, @PathVariable
            @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId, @BindUsingJackson @Valid SessionChannelContentsTextFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return sessionChannelContentsService.getChannelContentTexts(eventId, sessionId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ ROLE_EVN_MGR, ROLE_OPR_MGR })
    @RequestMapping(method = RequestMethod.POST, value = "/{sessionId}/channel-contents/texts", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateChannelContentsTexts(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId, @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
            @Valid @RequestBody SessionChannelContentTextListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        sessionChannelContentsService.updateChannelContentTexts(eventId, sessionId, contents.getTexts());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ ROLE_EVN_MGR, ROLE_OPR_MGR })
    @RequestMapping(method = RequestMethod.POST, value = "/channel-contents/texts", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateChannelContentsTextsBulk(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId, @Valid @RequestBody SessionChannelContentTextListBulkDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        sessionChannelContentsService.updateChannelContentTextsBulk(eventId, contents.getIds(), contents.getValues().getTexts());
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{sessionId}/channel-contents/images")
    public ChannelContentImageListDTO<SessionChannelContentImageType> getChannelContentsImages(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId, @BindUsingJackson @Valid EventChannelContentImageFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return sessionChannelContentsService.getChannelContentImages(eventId, sessionId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ ROLE_EVN_MGR, ROLE_OPR_MGR })
    @RequestMapping(method = RequestMethod.POST, value = "/{sessionId}/channel-contents/images", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateChannelContentsImages(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId, @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
            @Valid @RequestBody SessionChannelContentImageListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        sessionChannelContentsService.updateChannelContentImages(eventId, sessionId, contents);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ ROLE_EVN_MGR, ROLE_OPR_MGR })
    @RequestMapping(method = RequestMethod.POST, value = "/channel-contents/images", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateChannelContentsImagesBulk(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId, @Valid @RequestBody SessionChannelContentImageListBulkDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        sessionChannelContentsService.updateChannelContentImagesBulk(eventId, contents.getIds(), contents.getValues());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{sessionId}/channel-contents/images/languages/{language}/types/{type}")
    public void deleteChannelContentsImage(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
            @PathVariable @LanguageIETF String language,
            @PathVariable SessionChannelContentImageType type,
            @RequestParam(required = false) Integer position) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        if (SessionChannelContentImageType.LANDSCAPE.equals(type)) {
            ChannelContentsUtils.validatePosition(position);
        }
        sessionChannelContentsService.deleteChannelContentImages(eventId, sessionId, language, type, position);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/channel-contents/images/languages/{language}/types/{type}")
    public void deleteChannelContentsImageBulk(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @LanguageIETF String language,
            @PathVariable SessionChannelContentImageType type,
            @RequestParam(value = "session_id") @NotEmpty @NotNull List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds,
            @RequestParam(required = false) Integer position) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        if (SessionChannelContentImageType.LANDSCAPE.equals(type)) {
            ChannelContentsUtils.validatePosition(position);
        }
        sessionChannelContentsService.deleteChannelContentImageBulk(eventId, sessionIds, language, type, position);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/channel-contents/images/languages/{language}")
    public void deleteChannelContentsImagesBulk(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @LanguageIETF String language,
            @RequestParam(value = "session_id") @NotEmpty @NotNull List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        sessionChannelContentsService.deleteChannelContentImagesBulk(eventId, sessionIds, language);
    }
}
