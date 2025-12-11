package es.onebox.event.secondarymarket.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.secondarymarket.dto.CreateSessionSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.service.SessionSecondaryMarketConfigService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/secondary-market/session-config/{sessionId}")
public class SessionSecondaryMarketConfigController {

    private final SessionSecondaryMarketConfigService sessionSecondaryMarketConfigService;

    @Autowired
    public SessionSecondaryMarketConfigController(SessionSecondaryMarketConfigService sessionSecondaryMarketConfigService) {
        this.sessionSecondaryMarketConfigService = sessionSecondaryMarketConfigService;
    }

    @GetMapping()
    public SessionSecondaryMarketConfigDTO getSessionSecondaryMarketConfig(@PathVariable Long sessionId) {
        return sessionSecondaryMarketConfigService.getSessionSecondaryMarketConfig(sessionId);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createSessionSecondaryMarketConfig(@PathVariable Long sessionId, @RequestBody @Valid CreateSessionSecondaryMarketConfigDTO createSessionSecondaryMarketConfigDTO) {

        sessionSecondaryMarketConfigService.createSessionSecondaryMarketConfig(sessionId, createSessionSecondaryMarketConfigDTO);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSessionSecondaryMarketConfig(@PathVariable Long sessionId) {

        sessionSecondaryMarketConfigService.deleteSessionSecondaryMarketConfig(sessionId);
    }
}
