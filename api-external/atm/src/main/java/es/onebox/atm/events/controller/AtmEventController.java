package es.onebox.atm.events.controller;

import es.onebox.atm.events.service.AtmEventService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.datasources.ms.event.dto.PreSaleConfigDTO;
import es.onebox.common.security.Role;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(ApiConfig.ATMApiConfig.BASE_URL + "/events")
public class AtmEventController {
    private final AtmEventService atmEventService;

    @Autowired
    public AtmEventController(AtmEventService atmEventService) {
        this.atmEventService = atmEventService;
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @GetMapping(value = "/{eventId}/sessions/{sessionId}/presale")
    public PreSaleConfigDTO getSessionPresaleInformation(@PathVariable("eventId") @NotNull Long eventId,
                                                         @PathVariable("sessionId") @NotNull Long sessionId) {
        return atmEventService.getSessionPresaleInformation(eventId, sessionId);
    }
}
