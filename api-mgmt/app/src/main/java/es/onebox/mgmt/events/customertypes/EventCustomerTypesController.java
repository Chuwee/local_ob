package es.onebox.mgmt.events.customertypes;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.EventCustomerTypeDTO;
import es.onebox.mgmt.events.dto.UpdateEventCustomerTypesDTO;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(EventCustomerTypesController.BASE_URI)
public class EventCustomerTypesController {

    private static final String AUDIT_COLLECTION = "EVENT_CUSTOMER_TYPES";

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/customer-types";

    private final EventCustomerTypesService service;

    public EventCustomerTypesController(EventCustomerTypesService service) {
        this.service = service;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public List<EventCustomerTypeDTO> getEventCustomerTypes(@PathVariable(value = "eventId")
                                                            @Min(value = 1, message = "eventId must be above 0")
                                                            Integer eventId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getEventCustomerTypes(eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping
    public void putEventCustomerTypes(@PathVariable(value = "eventId")
                                      @Min(value = 1, message = "eventId must be above 0")
                                      Integer eventId, @RequestBody UpdateEventCustomerTypesDTO dto) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateEventCustomerTypes(eventId, dto);
    }
}
