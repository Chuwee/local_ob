package es.onebox.mgmt.countries.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.countries.CountriesRequestDTO;
import es.onebox.mgmt.countries.converter.CountryConverter;
import es.onebox.mgmt.countries.dto.CountryDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.repository.MasterdataRepository;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    @Autowired
    private MasterdataRepository masterdataRepository;

    public List<IdNameCodeDTO> getCountries(CountriesRequestDTO request) {
        return CountryConverter.fromEntities(masterdataRepository.getCountries(request.getCode(), request.getSystemCountry()));
    }

    public List<CountryDTO> getCountriesWithTaxCalculation(CountriesRequestDTO request) {
        return CountryConverter.fromCountriesWithTaxCalculation(masterdataRepository.getCountriesWithTaxCalculation(request.getCode(), request.getSystemCountry()));
    }

    public List<IdNameCodeDTO> getCountrySubdivision(String countryCode) {
        MasterdataValue country = masterdataRepository.getCountries(countryCode, null).stream().findFirst().orElse(null);
        if (country == null) {
            throw OneboxRestException.builder(ApiMgmtEntitiesErrorCode.COUNTRY_NOT_FOUND).build();
        }
        return CountryConverter.fromEntities(masterdataRepository.getCountrySubdivisionByCountryId(country.getId()));
    }

}
