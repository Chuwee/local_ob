package es.onebox.mgmt.events.surcharges;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.surcharges.dto.EventSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.EventSurchargeListDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.EventsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping(value = EventSurchargeController.BASE_URI)
public class EventSurchargeController {

    public static final String BASE_URI = EventsController.BASE_URI + "/{eventId}/surcharges";
    private static final String AUDIT_COLLECTION = "EVENT_SURCHARGES";

    private final EventSurchargeService eventSurchargeService;

    @Autowired
    public EventSurchargeController(EventSurchargeService eventSurchargeService) {
        this.eventSurchargeService = eventSurchargeService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @GetMapping()
    public List<EventSurchargeDTO> getSurcharges(@PathVariable Long eventId,
                                                 @RequestParam(value = "type", required = false) List<SurchargeTypeDTO> types) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return eventSurchargeService.getSurcharges(eventId, types);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setSurcharge(@PathVariable Long eventId, @RequestBody EventSurchargeListDTO eventSurchargeListDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        eventSurchargeService.setSurcharge(eventId, eventSurchargeListDTO);
    }
}
