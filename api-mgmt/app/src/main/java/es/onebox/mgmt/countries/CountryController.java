package es.onebox.mgmt.countries;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.countries.dto.CountryDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.countries.service.CountryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Valid
@RestController
@RequestMapping(ApiConfig.BASE_URL + "/countries")
public class CountryController {

    private static final String AUDIT_COUNTRIES = "COUNTRIES";
    private static final String AUDIT_COUNTRIES_SUBDIVISIONS = "COUNTRIES_SUBDIVISIONS";

    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<CountryDTO> getCountries(@BindUsingJackson CountriesRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COUNTRIES, AuditTag.AUDIT_ACTION_SEARCH);
        return countryService.getCountriesWithTaxCalculation(requestDTO);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{countryCode}/subdivisions")
    public List<IdNameCodeDTO> getCountrySubdivision(@PathVariable String countryCode) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COUNTRIES_SUBDIVISIONS, AuditTag.AUDIT_ACTION_SEARCH);
        return countryService.getCountrySubdivision(countryCode);
    }
}

