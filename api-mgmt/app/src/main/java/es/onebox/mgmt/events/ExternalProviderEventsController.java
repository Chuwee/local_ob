package es.onebox.mgmt.events;

import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.events.dto.ExternalEventBaseDTO;
import es.onebox.mgmt.events.dto.ExternalEventsProviderType;
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
@RequestMapping(ExternalProviderEventsController.BASE_URI)
public class ExternalProviderEventsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/external-provider-events";

    private final ExternalProviderEventsService externalProviderEventsService;

    @Autowired
    public ExternalProviderEventsController(ExternalProviderEventsService externalProviderEventsService) {
        this.externalProviderEventsService = externalProviderEventsService;
    }

    @GetMapping
    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public List<ExternalEventBaseDTO> getExternalEvents(
            @RequestParam(value = "entity_id") Long entityId,
            @RequestParam(value = "venue_template_id", required = false) Long venueTemplateId,
            @RequestParam(value = "type", required = false) ExternalEventsProviderType type,
            @RequestParam(value = "skip_used", required = false) Boolean skipUsed
    ) {
        return externalProviderEventsService.getExternalEvents(entityId, venueTemplateId, type, skipUsed);
    }
}

