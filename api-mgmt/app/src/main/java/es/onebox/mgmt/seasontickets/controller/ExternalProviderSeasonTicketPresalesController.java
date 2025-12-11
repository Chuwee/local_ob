package es.onebox.mgmt.seasontickets.controller;

import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.events.dto.ExternalPresaleBaseDTO;
import es.onebox.mgmt.seasontickets.service.ExternalProviderSeasonTicketPresalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(ExternalProviderSeasonTicketPresalesController.BASE_URI)
public class ExternalProviderSeasonTicketPresalesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/external-provider-presales";

    private final ExternalProviderSeasonTicketPresalesService externalProviderSeasonTicketPresalesService;

    @Autowired
    public ExternalProviderSeasonTicketPresalesController(ExternalProviderSeasonTicketPresalesService externalProviderSeasonTicketPresalesService) {
        this.externalProviderSeasonTicketPresalesService = externalProviderSeasonTicketPresalesService;
    }

    @GetMapping(value = "/season-tickets/{seasonTicketId}/external-presales")
    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public List<ExternalPresaleBaseDTO> getExternalProviderSeasonTicketPrivatePresales(
            @PathVariable(value = "seasonTicketId") Long seasonTicketId,
            @RequestParam(value = "skip_used", defaultValue = "false") boolean skipUsed
    ) {
        return externalProviderSeasonTicketPresalesService.getAllExternalPrivatePresales(seasonTicketId, skipUsed);
    }
}
