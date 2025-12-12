package es.onebox.internal.xmlsepa.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.Role;
import es.onebox.internal.config.InternalApiConfig;
import es.onebox.internal.xmlsepa.dto.SeasonTicketRequestDTO;
import es.onebox.internal.xmlsepa.service.sepa.SEPAGenerationService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(InternalApiConfig.XMLSepa.BASE_URL + "/generate")
public class SEPAGenerationController {

    private final SEPAGenerationService SEPAGenerationService;

    public SEPAGenerationController(SEPAGenerationService SEPAGenerationService) {
        this.SEPAGenerationService = SEPAGenerationService;
    }

    @Secured(Role.OPERATOR_MANAGER)
    @PostMapping
    public void generateRenewalXML(@RequestBody SeasonTicketRequestDTO body) {
        SEPAGenerationService.sendRenewalMessage(body.getSeasonTicketId());
    }
}