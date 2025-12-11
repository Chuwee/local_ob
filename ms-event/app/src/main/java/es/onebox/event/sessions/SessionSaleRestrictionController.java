package es.onebox.event.sessions;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.sessions.dto.PriceTypeRestrictionDTO;
import es.onebox.event.sessions.dto.UpdateSaleRestrictionDTO;
import es.onebox.event.sessions.service.SessionSaleRestrictionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(SessionSaleRestrictionController.COMMON_URL)
public class SessionSaleRestrictionController {

    public static final String COMMON_URL = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}";
    public static final String SALE_RESTRICTIONS_URL = "/price-types/{priceTypeId}/restrictions";
    public static final String RESTRICTIONS_URL = "/restricted-price-types";

    @Autowired
    private SessionSaleRestrictionsService sessionSaleRestrictionsService;

    private static final String SESSION_ID_MUST_BE_ABOVE_0 = "Session Id must be above 0";
    private static final String EVENT_ID_MUST_BE_ABOVE_0 = "Event Id must be above 0";
    private static final String LOCKED_PRICE_TYPE_ID_MUST_BE_ABOVE_0 = "Locked price type Id must be above 0";

    @RequestMapping(method = PUT,
            value = SALE_RESTRICTIONS_URL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void upsertSaleRestrictions(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                      @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                      @PathVariable @Min(value = 1, message = LOCKED_PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                      @RequestBody @Valid UpdateSaleRestrictionDTO request) {
        sessionSaleRestrictionsService.upsertSaleRestrictions(eventId, sessionId, priceTypeId, request);
    }

    @RequestMapping(method = GET, value = SALE_RESTRICTIONS_URL)
    public PriceTypeRestrictionDTO getSaleRestrictions(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                       @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                       @PathVariable @Min(value = 1, message = LOCKED_PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId) {
        return sessionSaleRestrictionsService.getSaleRestriction(eventId, sessionId, priceTypeId);
    }

    @RequestMapping(method = DELETE, value = SALE_RESTRICTIONS_URL)
    public void deleteSaleRestrictions(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                       @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                       @PathVariable @Min(value = 1, message = LOCKED_PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId) {
        sessionSaleRestrictionsService.deleteSaleRestrictions(eventId, sessionId, priceTypeId);
    }

    @RequestMapping(
            method = GET,
            value = RESTRICTIONS_URL,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IdNameDTO> getSessionRestrictedPriceTypes(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                          @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId) {
        return sessionSaleRestrictionsService.getRestrictedPriceTypes(eventId, sessionId);
    }

}
