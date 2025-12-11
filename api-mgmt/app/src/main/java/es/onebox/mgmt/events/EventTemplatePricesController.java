package es.onebox.mgmt.events;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.UpdateEventTemplatePriceRequestListDTO;
import es.onebox.mgmt.events.dto.VenueTemplatePriceExtendedDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
@Validated
@RequestMapping(EventTemplatePricesController.BASE_URI)
public class EventTemplatePricesController {

    static final String BASE_URI = EventsController.BASE_URI + "/{eventId}/venue-templates/{templateId}/prices";

    private static final String SESSION_ID_MUST_BE_ABOVE_0 = "Session Id must be above 0";
    private static final String GROUP_RATE_ID_MUST_BE_ABOVE_0 = "Group rate Id must be above 0";
    private static final String AUDIT_COLLECTION = "EVENT_TEMPLATE_PRICES";

    private final EventTemplatePricesService eventTemplatesService;

    @Autowired
    public EventTemplatePricesController(EventTemplatePricesService eventTemplatesService) {
        this.eventTemplatesService = eventTemplatesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public List<VenueTemplatePriceExtendedDTO> getVenueTemplatePrice(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                                         @PathVariable @Min(value = 1, message = "templateId must be above 0") Long templateId,
                                                                         @RequestParam(value = "session_id", required = false) List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIdList,
                                                                         @RequestParam(value = "rate_group_id", required = false) List<@Min(value = 1, message = GROUP_RATE_ID_MUST_BE_ABOVE_0) Integer> rateGroupList,
                                                                         @RequestParam(value = "rate_group_product_id", required = false) List<@Min(value = 1, message = GROUP_RATE_ID_MUST_BE_ABOVE_0) Integer> rateGroupProductList
                                                             ) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return eventTemplatesService.getVenueTemplatePrices(eventId, templateId, sessionIdList, rateGroupList, rateGroupProductList);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVenueTemplatePrice(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                         @PathVariable @Min(value = 1, message = "templateId must be above 0") Long templateId,
                                         @Valid @RequestBody UpdateEventTemplatePriceRequestListDTO prices) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        eventTemplatesService.updateVenueTemplatePrices(eventId, templateId, prices.getPrices());
    }

}
