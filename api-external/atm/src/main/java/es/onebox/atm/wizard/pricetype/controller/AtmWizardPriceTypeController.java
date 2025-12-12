package es.onebox.atm.wizard.pricetype.controller;


import es.onebox.atm.wizard.pricetype.dto.PriceTypeMappingRequest;
import es.onebox.atm.wizard.pricetype.service.AtmWizardPriceTypeMappingService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(ApiConfig.ATMApiConfig.BASE_URL + "/wizard/events/{eventId}/venue-templates/{venueTemplateId}/price-type-mappings")
public class AtmWizardPriceTypeController {

    private final AtmWizardPriceTypeMappingService priceTypeMappingService;

    @Autowired
    public AtmWizardPriceTypeController(AtmWizardPriceTypeMappingService priceTypeMappingService) {
        this.priceTypeMappingService = priceTypeMappingService;
    }

    @Secured({Role.OPERATOR_MANAGER, Role.ENTITY_MANAGER, Role.EVENT_MANAGER})
    @PutMapping()
    public void setUpPriceTypeCodes(@PathVariable("eventId") Long eventId,
                                    @PathVariable("venueTemplateId") Long venueTemplateId,
                                    @RequestBody @Valid @NotNull PriceTypeMappingRequest request) {
        priceTypeMappingService.setUpPriceTypeCodes(eventId, venueTemplateId, request);
    }
}
