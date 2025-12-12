package es.onebox.circuitcat.venues.controller;

import es.onebox.circuitcat.venues.dto.VenueConfigDTO;
import es.onebox.circuitcat.venues.service.VenueService;
import es.onebox.common.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiConfig.CircuitApiConfig.BASE_URL + "/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @GetMapping()
    public VenueConfigDTO getVenueConfig(@RequestParam(value = "session_ids", required = true) List<Long> sessionIds) {

        return venueService.getVenueConfig(sessionIds);
    }
}
