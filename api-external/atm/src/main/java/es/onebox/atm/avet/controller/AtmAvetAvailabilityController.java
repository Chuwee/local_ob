package es.onebox.atm.avet.controller;


import es.onebox.atm.avet.service.AtmAvetAvailabilityService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.datasources.dispatcher.dto.CheckStatusResponse;
import es.onebox.common.security.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(ApiConfig.ATMApiConfig.BASE_URL + "/avet-availability")
public class AtmAvetAvailabilityController {

    private final AtmAvetAvailabilityService atmAvetAvailabilityService;

    @Autowired
    public AtmAvetAvailabilityController(AtmAvetAvailabilityService atmAvetAvailabilityService){
        this.atmAvetAvailabilityService = atmAvetAvailabilityService;
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @GetMapping()
    public CheckStatusResponse getAvetAvailability(HttpServletRequest request) {
        return atmAvetAvailabilityService.getAvetAvailability();
    }

}

