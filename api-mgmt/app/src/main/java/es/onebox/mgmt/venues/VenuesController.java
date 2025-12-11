package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.venues.dto.SearchCityVenuesResponse;
import es.onebox.mgmt.venues.dto.SearchCountryVenuesResponse;
import es.onebox.mgmt.venues.dto.SearchVenuesResponse;
import es.onebox.mgmt.venues.dto.VenueDetailsDTO;
import es.onebox.mgmt.venues.dto.VenueItemPostRequestDTO;
import es.onebox.mgmt.venues.dto.VenueItemPutRequestDTO;
import es.onebox.mgmt.venues.dto.VenueSearchAggFilter;
import es.onebox.mgmt.venues.dto.VenueSearchFilter;
import es.onebox.mgmt.venues.service.VenuesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;

@RestController
@Validated
@RequestMapping(
        value = VenuesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class VenuesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venues";

    private static final String AUDIT_COLLECTION = "VENUES";
    private static final String AUDIT_CITIES_COLLECTION = "VENUES_CITIES";
    private static final String AUDIT_COUNTRIES_COLLECTION = "VENUES_COUNTRIES";

    private final VenuesService venuesService;

    @Autowired
    public VenuesController(VenuesService venuesService){
        this.venuesService = venuesService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{venueId}")
    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public VenueDetailsDTO getVenue(@PathVariable(value = "venueId") Long venueId, @BindUsingJackson @Valid VenueSearchFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return venuesService.getVenue(venueId, filter);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public SearchVenuesResponse getVenues(@BindUsingJackson @Valid VenueSearchFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return venuesService.getVenues(filter);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/all/cities")
    public SearchCityVenuesResponse getCitiesVenues(@BindUsingJackson @Valid VenueSearchAggFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_CITIES_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return venuesService.getCitiesVenues(filter);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/all/countries")
    public SearchCountryVenuesResponse getCountriesVenues(@BindUsingJackson @Valid VenueSearchAggFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COUNTRIES_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return venuesService.getCountriesVenues(filter);
    }

    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createVenue(@BindUsingJackson @Valid @RequestBody VenueItemPostRequestDTO newVenue){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return venuesService.createVenue(newVenue);
    }

    @PutMapping(value = "{venueId}")
    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVenue(@PathVariable Long venueId,
                            @BindUsingJackson @Valid @RequestBody VenueItemPutRequestDTO request){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        request.setId(venueId);
        venuesService.updateVenue(request);
    }

    @DeleteMapping(value = "{venueId}")
    @Secured({ROLE_REC_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenue(@PathVariable Long venueId){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        venuesService.deleteVenue(venueId);
    }
}
