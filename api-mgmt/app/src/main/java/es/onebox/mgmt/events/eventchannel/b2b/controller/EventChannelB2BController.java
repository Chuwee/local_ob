package es.onebox.mgmt.events.eventchannel.b2b.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.eventchannel.b2b.dto.EventChannelB2BAssignationsDTO;
import es.onebox.mgmt.events.eventchannel.b2b.service.EventChannelB2BService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(value = EventChannelB2BController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class EventChannelB2BController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/channels/{channelId}/b2b";
    private static final String AUDIT_COLLECTION = "EVENTS_CHANNELS_B2B";

    private final EventChannelB2BService eventChannelB2BService;

    @Autowired
    public EventChannelB2BController(EventChannelB2BService eventChannelB2BService) {
        this.eventChannelB2BService = eventChannelB2BService;
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_EVN_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/assignations")
    public EventChannelB2BAssignationsDTO getEventChannelB2BAssignations(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                                         @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return eventChannelB2BService.getAssignations(eventId, channelId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_EVN_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/assignations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEventChannelB2BAssignations(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                                            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                                            @RequestBody @NotNull @Valid EventChannelB2BAssignationsDTO assignations) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        eventChannelB2BService.updateAssignation(eventId, channelId, assignations);
    }
}
