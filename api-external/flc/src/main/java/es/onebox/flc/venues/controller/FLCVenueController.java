package es.onebox.flc.venues.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.flc.common.GenericRequest;
import es.onebox.flc.venues.dto.Venue;
import es.onebox.flc.venues.service.FLCVenueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Deprecated
@RestController
@RequestMapping(ApiConfig.FLCApiConfig.BASE_URL + "/venues")
public class FLCVenueController {

    @Autowired
    private FLCVenueService venueService;

    @GetMapping()
    public List<Venue> getVenues(@RequestParam(value = "in_use", required = true) Boolean inUse,
                                 final @Valid @BindUsingJackson GenericRequest request) {

        return venueService.getVenues(inUse, request.getLimit(), request.getOffset());
    }
}
