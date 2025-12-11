package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.VenueTemplatePriceDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketPricesRequestListDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketPricesService;
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
@RequestMapping(SeasonTicketController.BASE_URI)
public class SeasonTicketPricesController {

    private final SeasonTicketPricesService seasonTicketPricesService;
    private static final String AUDIT_COLLECTION = "SEASON_TICKET_PRICES";

    @Autowired
    public SeasonTicketPricesController(SeasonTicketPricesService seasonTicketPricesService) {
        this.seasonTicketPricesService = seasonTicketPricesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{seasonTicketId}/prices")
    public List<VenueTemplatePriceDTO> getPrices(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return seasonTicketPricesService.getPrices(seasonTicketId);

    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping( "/{seasonTicketId}/prices")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePrices(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                             @Valid @RequestBody UpdateSeasonTicketPricesRequestListDTO prices) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        seasonTicketPricesService.updatePrices(seasonTicketId, prices.getPrices());
    }
}
