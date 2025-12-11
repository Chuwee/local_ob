package es.onebox.mgmt.sessions;

import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.events.dto.ExternalSessionBaseDTO;
import es.onebox.mgmt.events.dto.ExternalSessionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(ExternalProviderSessionsController.BASE_URI)
public class ExternalProviderSessionsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/external-provider-sessions";

    private final ExternalProviderSessionsService externalProviderSessionsService;

    @Autowired
    public ExternalProviderSessionsController(ExternalProviderSessionsService externalProviderSessionsService) {
        this.externalProviderSessionsService = externalProviderSessionsService;
    }

    @GetMapping
    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public List<ExternalSessionBaseDTO> getExternalSessions(
            @RequestParam(value = "entity_id") Long entityId,
            @RequestParam(value = "event_id", required = false) Long eventId,
            @RequestParam(value = "status", required = false) ExternalSessionStatus status,
            @RequestParam(value = "skip_used", required = false) Boolean skipUsed
    ) {
        return externalProviderSessionsService.getExternalSessions(entityId, eventId, status, skipUsed);
    }
}

