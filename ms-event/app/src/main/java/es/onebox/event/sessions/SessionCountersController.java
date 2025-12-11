package es.onebox.event.sessions;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.sessions.dto.SessionCustomersActionType;
import es.onebox.event.sessions.service.SessionCountersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}")
public class SessionCountersController {

    private final SessionCountersService sessionCountersService;

    @Autowired
    public SessionCountersController(SessionCountersService sessionCountersService) {
        this.sessionCountersService = sessionCountersService;
    }

    @GetMapping(value = "/presales-customers/{customerId}/counters")
    public Long getSessionPresaleCustomerCounter(
            @PathVariable(value = "eventId") Integer eventId,
            @PathVariable(value = "sessionId") Integer sessionId,
            @PathVariable(value = "customerId") String customerId) {
        return sessionCountersService.getPresaleCustomerCounter(sessionId, customerId);
    }

    @PutMapping(value = "/presales-customers/{customerId}/counters")
    public Long updateSessionPresaleCustomerCounter(
            @PathVariable(value = "eventId") Integer eventId,
            @PathVariable(value = "sessionId") Integer sessionId,
            @PathVariable(value = "customerId") String customerId,
            @RequestParam(value = "amount", required = false) Integer amount,
            @RequestParam("actionType") SessionCustomersActionType actionType) {
        return sessionCountersService.updatePresaleCustomerCounter(sessionId, customerId, amount, actionType);
    }

    @GetMapping(value = "/price-types/{priceTypeId}/customers/{customerId}/counters")
    public Long getSessionPriceTypeCustomerCounter(
            @PathVariable(value = "eventId") Integer eventId,
            @PathVariable(value = "sessionId") Integer sessionId,
            @PathVariable(value = "priceTypeId") Integer priceTypeId,
            @PathVariable(value = "customerId") String customerId) {
        return sessionCountersService.getSessionCustomerCounter(sessionId, priceTypeId, customerId);
    }

    @PutMapping(value = "/price-types/{priceTypeId}/customers/{customerId}/counters")
    public Long updateSessionPriceTypeCustomerCounter(
            @PathVariable(value = "eventId") Integer eventId,
            @PathVariable(value = "sessionId") Integer sessionId,
            @PathVariable(value = "priceTypeId") Integer priceTypeId,
            @PathVariable(value = "customerId") String customerId,
            @RequestParam(value = "amount", required = false) Integer amount,
            @RequestParam("actionType") SessionCustomersActionType actionType) {
        return sessionCountersService.updateSessionCustomerCounter(sessionId, priceTypeId, customerId, amount, actionType);
    }

    @GetMapping(value = "/secondary-market/customers/{customerId}/counters")
    public Long getSecondaryMarketCustomerCounter(
            @PathVariable(value = "eventId") Integer eventId,
            @PathVariable(value = "sessionId") Integer sessionId,
            @PathVariable(value = "customerId") String customerId) {
        return sessionCountersService.getSecondaryMarketCustomerCounter(sessionId, customerId);
    }

    @PutMapping(value = "/secondary-market/customers/{customerId}/counters")
    public Long updateSecondaryMarketCustomerCounter(
            @PathVariable(value = "eventId") Integer eventId,
            @PathVariable(value = "sessionId") Integer sessionId,
            @PathVariable(value = "customerId") String customerId,
            @RequestParam(value = "amount", required = false) Integer amount,
            @RequestParam("actionType") SessionCustomersActionType actionType) {
        return sessionCountersService.updateSecondaryMarketCustomerCounter(sessionId, customerId, amount, actionType);
    }

}
