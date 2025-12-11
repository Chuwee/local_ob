package es.onebox.mgmt.secondarymarket.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.secondarymarket.dto.SecondaryMarketConfigDTO;
import es.onebox.mgmt.secondarymarket.service.EventsSecondaryMarketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping(value = EventsSecondaryMarketController.BASE_URI)
public class EventsSecondaryMarketController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/secondary-market";
    private static final String AUDIT_COLLECTION = "SECONDARY_MARKET";

    private final EventsSecondaryMarketService eventsSecondaryMarketService;

    @Autowired
    public EventsSecondaryMarketController(EventsSecondaryMarketService eventsSecondaryMarketService) {
        this.eventsSecondaryMarketService = eventsSecondaryMarketService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @GetMapping
    public SecondaryMarketConfigDTO getEventSecondaryMarketConfig(@PathVariable Long eventId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return eventsSecondaryMarketService.getEventSecondaryMarketConfig(eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createSecondaryMarket(@PathVariable Long eventId, @Valid @RequestBody SecondaryMarketConfigDTO secondaryMarketConfigDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        eventsSecondaryMarketService.createEventSecondaryMarketConfig(eventId, secondaryMarketConfigDTO);
    }


}
