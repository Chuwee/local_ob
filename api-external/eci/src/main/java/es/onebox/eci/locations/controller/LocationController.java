package es.onebox.eci.locations.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.eci.common.GenericRequest;
import es.onebox.eci.locations.dto.Location;
import es.onebox.eci.locations.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping(ApiConfig.ECIApiConfig.BASE_URL + "/{channelIdentifier}/locations")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping()
    public List<Location> getLocations(@RequestParam(value = "session_start_date[gte]", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime gte,
                                       @RequestParam(value = "session_start_date[lte]", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime lte,
                                       @PathVariable("channelIdentifier") String channelIdentifier,
                                       final @Valid @BindUsingJackson GenericRequest request) {
        return locationService.getLocations(gte, lte, request.getLimit(), request.getOffset(), channelIdentifier);
    }

    @GetMapping(value = "/{locationId}")
    public Location getLocations(@PathVariable("channelIdentifier") String channelIdentifier, @PathVariable("locationId") Long locationId) {
        return locationService.getLocation(channelIdentifier, locationId);
    }
}
