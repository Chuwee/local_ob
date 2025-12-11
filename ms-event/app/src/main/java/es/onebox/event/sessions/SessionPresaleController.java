package es.onebox.event.sessions;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.sessions.dto.CreateSessionPreSaleConfigDTO;
import es.onebox.event.sessions.dto.SessionPreSaleConfigDTO;
import es.onebox.event.sessions.dto.UpdateSessionPreSaleConfigDTO;
import es.onebox.event.sessions.service.SessionPresaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/presales")
public class SessionPresaleController {

    private final SessionPresaleService sessionPresaleService;

    @Autowired
    public SessionPresaleController(SessionPresaleService sessionPresaleService) {
        this.sessionPresaleService = sessionPresaleService;
    }

    @GetMapping
    public List<SessionPreSaleConfigDTO> getSessionPresales(@PathVariable(value = "eventId") Long eventId,
                                                            @PathVariable(value = "sessionId") Long sessionId) {

        return sessionPresaleService.getSessionPresales(sessionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionPreSaleConfigDTO createSessionPresale(@PathVariable(value = "eventId") Long eventId,
                                                        @PathVariable(value = "sessionId") Long sessionId,
                                                        @Valid @NotNull @RequestBody CreateSessionPreSaleConfigDTO request) {

        return sessionPresaleService.createSessionPresale(eventId, sessionId, request);
    }

    @PutMapping(value = "/{presaleId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateSessionPresale(@PathVariable(value = "eventId") Long eventId,
                                     @PathVariable(value = "sessionId") Long sessionId,
                                     @PathVariable(value = "presaleId") Long presaleId,
                                     @Valid @NotNull @RequestBody UpdateSessionPreSaleConfigDTO request) {

        sessionPresaleService.updateSessionPresale(eventId, sessionId, presaleId, request);
    }

    @DeleteMapping(value = "/{presaleId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSessionPresale(@PathVariable(value = "eventId") Long eventId,
                                     @PathVariable(value = "sessionId") Long sessionId,
                                     @PathVariable(value = "presaleId") Long presaleId) {

        sessionPresaleService.deleteSessionPresale(sessionId, presaleId);
    }

}
