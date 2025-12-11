package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.sessions.dto.PriceTypeDTO;
import es.onebox.mgmt.sessions.dto.PriceTypeRequestDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = SessionPriceTypesController.BASE_URI)
@Validated
public class SessionPriceTypesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/price-types";

    public static final String EVENT_ID = "event_id";
    public static final String SESSION_ID = "session_id";
    private static final String AUDIT_COLLECTION = "SESSION_PRICE_TYPES";

    @Autowired
    private SessionPriceTypesService service;

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public List<PriceTypeDTO> getSessionPriceTypes(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "sessionId must be above 0") Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return service.getSessionPriceTypes(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{priceTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putSessionPriceTypes(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                     @PathVariable @Min(value = 1, message = "sessionId must be above 0") Long sessionId,
                                     @PathVariable Long priceTypeId,
                                     @RequestBody @NotNull PriceTypeRequestDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        service.upsert(eventId, sessionId, priceTypeId, request);
    }

}
