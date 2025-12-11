package es.onebox.event.sessions;

import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.RateRestrictedDTO;
import es.onebox.event.events.dto.UpdateRateRestrictionsDTO;
import es.onebox.event.sessions.service.SessionRateRestrictionsService;
import es.onebox.event.sessions.service.SessionRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/rates")
public class SessionRateController {

    private static final String RATE_ID = "/{rateId}";
    private static final String RESTRICTIONS = "/restrictions";
    private static final String RATE_ID_RESTRICTIONS = RATE_ID + RESTRICTIONS;
    private final SessionRateService sessionRateService;
    private final RefreshDataService refreshDataService;
    private final SessionRateRestrictionsService sessionRateRestrictionsService;

    @Autowired
    public SessionRateController(
            SessionRateService sessionRateService,
            RefreshDataService refreshDataService,
            SessionRateRestrictionsService sessionRateRestrictionsService
    ) {
        this.sessionRateService = sessionRateService;
        this.refreshDataService = refreshDataService;
        this.sessionRateRestrictionsService = sessionRateRestrictionsService;
    }

    @RequestMapping(
            value = RATE_ID,
            method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createSessionRate(@PathVariable(value = "eventId") Integer eventId,
                                  @PathVariable(value = "sessionId") Integer sessionId,
                                  @PathVariable(value = "rateId") Integer rateId) {

        sessionRateService.createSessionRate(sessionId, rateId);
        refreshDataService.refreshSession(sessionId.longValue(), "createSessionRate");
    }

    @RequestMapping(
            value = RATE_ID,
            method = DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteSessionRate(@PathVariable(value = "eventId") Integer eventId,
                                  @PathVariable(value = "sessionId") Integer sessionId,
                                  @PathVariable(value = "rateId") Integer rateId) {

        sessionRateService.deleteSessionRate(sessionId, rateId);
        refreshDataService.refreshSession(sessionId.longValue(), "deleteSessionRate");
    }

    @RequestMapping(
            value = RATE_ID_RESTRICTIONS,
            method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void upsertSessionRatesRestrictions(@PathVariable(value = "eventId") Long eventId,
                                               @PathVariable(value = "sessionId") Long sessionId,
                                               @PathVariable(value = "rateId") Integer rateId,
                                               @RequestBody UpdateRateRestrictionsDTO restrictions) {
        sessionRateRestrictionsService.upsertSessionRateRestrictions(eventId, sessionId, rateId, restrictions);
        refreshDataService.refreshSession(sessionId, "upsertSessionRatesRestrictions");
    }

    @RequestMapping(
            value = RATE_ID_RESTRICTIONS,
            method = DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteSessionRatesRestrictions(@PathVariable(value = "eventId") Long eventId,
                                               @PathVariable(value = "sessionId") Long sessionId,
                                               @PathVariable(value = "rateId") Integer rateId) {
        sessionRateRestrictionsService.deleteSessionRateRestrictions(eventId, sessionId, rateId);
        refreshDataService.refreshSession(sessionId, "deleteSessionRatesRestrictions");
    }

    @RequestMapping(
            value = RESTRICTIONS,
            method = GET)
    @ResponseStatus(HttpStatus.OK)
    public List<RateRestrictedDTO> getRestrictedRates(@PathVariable(value = "eventId") Long eventId,
                                                      @PathVariable(value = "sessionId") Long sessionId) {
        return sessionRateRestrictionsService.getRestrictedRates(eventId, sessionId);
    }

}
