package es.onebox.event.sessions;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.sessions.dto.DynamicPriceListDTO;
import es.onebox.event.sessions.dto.DynamicPriceZoneDTO;
import es.onebox.event.sessions.dto.DynamicRatesPriceDTO;
import es.onebox.event.sessions.dto.SessionDynamicPriceConfigDTO;
import es.onebox.event.sessions.dto.external.DynamicPriceStatusRequest;
import es.onebox.event.sessions.service.SessionDynamicPriceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/dynamic-prices")
public class SessionDynamicPriceController {

    private final SessionDynamicPriceService sessionDynamicPriceService;

    @Autowired
    public SessionDynamicPriceController(SessionDynamicPriceService sessionDynamicPriceService) {
        this.sessionDynamicPriceService = sessionDynamicPriceService;
    }

    @PutMapping
    public void updateSessionDynamicPriceConfig(@PathVariable(value = "eventId") Long eventId,
                                                @PathVariable(value = "sessionId") Long sessionId,
                                                @NotNull @RequestBody DynamicPriceStatusRequest request) {
        sessionDynamicPriceService.updateActivationDynamicPrice(eventId, sessionId, request.getStatus());
    }

    @GetMapping
    public SessionDynamicPriceConfigDTO getSessionDynamicPriceConfig(@PathVariable(value = "eventId") Long eventId,
                                                                     @PathVariable(value = "sessionId") Long sessionId,
                                                                     @RequestParam(value = "initialize") Boolean initialize) {
        return sessionDynamicPriceService.getSessionDynamicPriceConfig(sessionId, initialize);
    }

    @GetMapping("/price-zones/{idPriceZone}")
    public DynamicPriceZoneDTO getDynamicPriceZone(@PathVariable(value = "eventId") Long eventId,
                                                   @PathVariable(value = "sessionId") Long sessionId,
                                                   @PathVariable(value = "idPriceZone") Long idPriceZone) {
        return  sessionDynamicPriceService.getActive(eventId, sessionId, idPriceZone);
    }

    @GetMapping("/price-zones/{idPriceZone}/rates")
    public List<DynamicRatesPriceDTO> getDynamicRatePrice(@PathVariable(value = "eventId") Long eventId,
                                                          @PathVariable(value = "sessionId") Long sessionId,
                                                          @PathVariable(value = "idPriceZone") Long idPriceZone) {
        return sessionDynamicPriceService.getDynamicRatePrice(sessionId, idPriceZone);
    }

    @PostMapping("/price-zones/{idPriceZone}")
    public void createOrUpdateDynamicPrice(@PathVariable(value = "eventId") Long eventId,
                                           @PathVariable(value = "sessionId") Long sessionId,
                                   @PathVariable(value = "idPriceZone") Long idPriceZone,
                                   @Valid @NotNull @RequestBody DynamicPriceListDTO request) {
        sessionDynamicPriceService.createOrUpdateSessionDynamicPrices(sessionId, idPriceZone, request);
    }


    @DeleteMapping("/price-zones/{idPriceZone}/zone/{orderId}")
    public void deleteDynamicPrice(@PathVariable(value = "eventId") Long eventId,
                                   @PathVariable(value = "sessionId") Long sessionId,
                                   @PathVariable(value = "idPriceZone") Long idPriceZone,
                                   @PathVariable(value = "orderId") Long orderId) {
        sessionDynamicPriceService.deleteSessionDynamicPrice(sessionId, idPriceZone, orderId.intValue());
    }
}
