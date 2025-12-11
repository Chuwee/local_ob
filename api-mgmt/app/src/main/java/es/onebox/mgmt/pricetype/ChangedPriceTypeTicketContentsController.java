package es.onebox.mgmt.pricetype;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ApiConfig.BASE_URL + "/venue-templates/{venueTemplateId}/ticket-contents/changed-price-types")
public class ChangedPriceTypeTicketContentsController {

    private static final String VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0 = "Venue Template Id must be above 0";
    private static final String AUDIT_COLLECTION = "CHANGEDPRICETYPE_TICKETCOMMUNICATIONELEMENTS";

    private final PriceTypeTicketContentsService priceTypeTicketContentsService;

    @Autowired
    public ChangedPriceTypeTicketContentsController(PriceTypeTicketContentsService priceTypeTicketContentsService) {
        this.priceTypeTicketContentsService = priceTypeTicketContentsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public List<IdNameDTO> getChangedPriceTypesTicketContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.priceTypeTicketContentsService.getChangedPriceTypeTicketContents(venueTemplateId);
    }

}
