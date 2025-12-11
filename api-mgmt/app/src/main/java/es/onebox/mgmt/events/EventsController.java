package es.onebox.mgmt.events;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.AdditionalConfigMatchesDTO;
import es.onebox.mgmt.events.dto.CreateEventRequestDTO;
import es.onebox.mgmt.events.dto.EventDTO;
import es.onebox.mgmt.events.dto.EventSearchFilterDTO;
import es.onebox.mgmt.events.dto.SearchEventsResponse;
import es.onebox.mgmt.events.dto.UpdateEventRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = EventsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class EventsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/events";

    private static final String AUDIT_COLLECTION = "EVENTS";

    private final EventsService eventsService;

    @Autowired
    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public SearchEventsResponse getEvents(@BindUsingJackson @Valid EventSearchFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return eventsService.searchEvents(filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{eventId}")
    public EventDTO getEvent(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        ConverterUtils.checkField(eventId, "eventId");
        return eventsService.getEvent(eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdDTO> createEvent(@Valid @RequestBody CreateEventRequestDTO eventData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        Long eventId = eventsService.createEvent(eventData);
        return new ResponseEntity<>(new IdDTO(eventId), HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(path="/{eventId}" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateEvent(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @Valid @RequestBody UpdateEventRequestDTO eventData){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        eventsService.updateEvent(eventId, eventData);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        eventsService.delete(eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{eventId}/additional-config")
    public AdditionalConfigMatchesDTO getEventAdditionalConfig(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return eventsService.getEventAdditionalConfig(eventId);
    }
}
