package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.sessions.dto.SessionSaleRestrictionDTO;
import es.onebox.mgmt.sessions.dto.SessionSaleRestrictionsDTO;
import es.onebox.mgmt.sessions.dto.UpdateSaleRestrictionDTO;
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

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = SessionSaleRestrictionsController.BASE_URI)
@Validated
public class SessionSaleRestrictionsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}";

    private static final String PRICE_TYPE_URL = "/price-types/{priceTypeId}/restrictions";
    private static final String RESTRICTIONS_URL = "/restricted-price-types";

    private static final String AUDIT_COLLECTION = "SESSION_SALE_RESTRICTIONS";
    private static final String SESSION_ID_MUST_BE_ABOVE_0 = "Session Id must be above 0";
    private static final String EVENT_ID_MUST_BE_ABOVE_0 = "Event Id must be above 0";
    private static final String PRICE_TYPE_ID_MUST_BE_ABOVE_0 = "Locked price type Id must be above 0";

    @Autowired
    private SessionSaleRestrictionsService service;

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = PRICE_TYPE_URL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void upsertRestriction(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                  @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                  @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                  @RequestBody @Valid UpdateSaleRestrictionDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.upsertRestriction(eventId, sessionId, priceTypeId, request);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = PRICE_TYPE_URL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRestriction(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        service.deleteRestriction(eventId, sessionId, priceTypeId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = PRICE_TYPE_URL)
    public SessionSaleRestrictionDTO getRestriction(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                  @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                                  @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getRestriction(eventId, sessionId, priceTypeId);

    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = RESTRICTIONS_URL)
    public SessionSaleRestrictionsDTO getRestriction(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                     @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getSessionRestrictions(eventId, sessionId);

    }

}
