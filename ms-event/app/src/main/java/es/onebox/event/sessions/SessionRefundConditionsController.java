package es.onebox.event.sessions;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.sessions.dto.SessionRefundConditionsDTO;
import es.onebox.event.sessions.service.SessionRefundConditionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@Validated
@RequestMapping(SessionRefundConditionsController.REFUND_CONDITIONS_URL)
public class SessionRefundConditionsController {

    public static final String REFUND_CONDITIONS_URL = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/refund-conditions";

    @Autowired
    private SessionRefundConditionsService sessionRefundConditionsService;


    @RequestMapping(method = GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SessionRefundConditionsDTO getRefundConditions(@PathVariable Long eventId, @PathVariable Long sessionId) {
        return sessionRefundConditionsService.getRefundConditions(eventId, sessionId);
    }

    @RequestMapping(method = PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRefundConditions(@PathVariable Long eventId, @PathVariable Long sessionId,
                                    @RequestBody @NotNull SessionRefundConditionsDTO updateRequest) {
        sessionRefundConditionsService.updateRefundConditions(eventId,sessionId,updateRequest);
    }

}
