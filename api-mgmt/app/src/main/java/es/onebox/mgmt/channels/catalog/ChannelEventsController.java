package es.onebox.mgmt.channels.catalog;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventFilter;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventUpdateDetailDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventsDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventsUpdateDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelSessionsDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelSessionsFilter;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

@Validated
@RestController
@RequestMapping(value = ChannelEventsController.BASE_URI)
public class ChannelEventsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/events";

    private static final String AUDIT_COLLECTION = "CHANNEL_EVENTS";

    private final ChannelEventsService service;

    @Autowired
    public ChannelEventsController(ChannelEventsService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public ChannelEventsDTO search(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @BindUsingJackson ChannelEventFilter request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return this.service.search(channelId, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @RequestBody @Valid ChannelEventsUpdateDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.update(channelId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET,value = "/{eventId}")
    public ChannelEventDTO getChannelEvent(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getChannelEvent(channelId, eventId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT,value = "/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putChannelEvent(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @RequestBody ChannelEventUpdateDetailDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.putChannelEvent(channelId, eventId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/{eventId}/sessions")
    public ChannelSessionsDTO searchSessions(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @BindUsingJackson ChannelSessionsFilter request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return this.service.searchSessions(channelId, eventId, request);
    }
}
