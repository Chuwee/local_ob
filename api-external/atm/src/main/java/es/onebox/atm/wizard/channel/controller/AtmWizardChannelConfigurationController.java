package es.onebox.atm.wizard.channel.controller;


import es.onebox.atm.wizard.channel.dto.ChannelConfigurationRequest;
import es.onebox.atm.wizard.channel.service.AtmWizardChannelConfigService;
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
@RequestMapping(ApiConfig.ATMApiConfig.BASE_URL + "/wizard/events/{eventId}/channels-configuration")
public class AtmWizardChannelConfigurationController {

    private AtmWizardChannelConfigService channelConfigService;

    @Autowired
    public AtmWizardChannelConfigurationController(AtmWizardChannelConfigService channelConfigService){
        this.channelConfigService = channelConfigService;
    }

    @Secured({Role.OPERATOR_MANAGER, Role.ENTITY_MANAGER, Role.EVENT_MANAGER})
    @PutMapping()
    public void setUpChannelConfiguration(@PathVariable("eventId") Long eventId,
                                    @RequestBody @Valid @NotNull ChannelConfigurationRequest request) {
       channelConfigService.setUpChannelConfig(eventId, request);
    }
}
