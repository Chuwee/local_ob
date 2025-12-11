package es.onebox.mgmt.externalevents.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.externalevents.dto.ExternalEventTypeDTO;
import es.onebox.mgmt.externalevents.dto.ExternalEventsResponse;
import es.onebox.mgmt.externalevents.service.ExternalEventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = ExternalEventsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ExternalEventsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/external-events";
    private static final String AUDIT_COLLECTION = "EXTERNAL_EVENTS";

    private final ExternalEventsService service;

    @Autowired
    public ExternalEventsController(ExternalEventsService service) {
        this.service = service;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public ExternalEventsResponse getExternalEvents(@RequestParam(required = false, value = "entity_id") Long entityId,
                                                   @RequestParam(required = false, value = "event_type") ExternalEventTypeDTO eventTypeDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return service.getExternalEvents(entityId, eventTypeDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{internalId}/rates")
    public List<IdNameDTO> getExternalEventRates(@PathVariable Long internalId) {
        Audit.addTags(AUDIT_SERVICE, "EXTERNAL_EVENT_RATES", AuditTag.AUDIT_ACTION_SEARCH);
        return service.getExternalEventRates(internalId);
    }
}
