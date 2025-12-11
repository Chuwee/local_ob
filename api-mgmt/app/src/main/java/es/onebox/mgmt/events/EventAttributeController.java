package es.onebox.mgmt.events;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.dto.AttributeDTO;
import es.onebox.mgmt.entities.dto.AttributeRequestValuesDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.BooleanUtils;
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
@RequestMapping(
        value = EventAttributeController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class EventAttributeController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/attributes";

    private static final String AUDIT_COLLECTION = "EVENT_ATTRIBUTES";

    @Autowired
    private EventsService eventsService;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public List<AttributeDTO> getEventAttributes(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                 @RequestParam(value="full_load", required = false) Boolean fullLoad) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return eventsService.getEventAttributes(eventId, BooleanUtils.isTrue(fullLoad));
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putEventAttributesValue(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                        @Valid @RequestBody AttributeRequestValuesDTO attributeRequestValuesDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        eventsService.putEventAttributeValue(eventId, attributeRequestValuesDTO);
    }

}
