package es.onebox.mgmt.common;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.common.dto.TimeZone;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.ResourceServer;
import es.onebox.mgmt.datasources.ms.entity.repository.MasterdataRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MasterdataService {

    private final MasterdataRepository masterdataRepository;

    @Autowired
    public MasterdataService(MasterdataRepository masterdataRepository) {
        this.masterdataRepository = masterdataRepository;
    }

    public MasterdataValue getLanguage(Long languageId) {
        return masterdataRepository.getLanguage(languageId);
    }

    public Integer getLanguageByCode(String languageCode) {
        final List<MasterdataValue> languages = masterdataRepository.getLanguages(languageCode, null);
        if (languages.isEmpty()) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.NOT_FOUND)
                    .setMessage("language_code not found").build();
        }
        return languages.get(0).getId().intValue();
    }

    public Map<Long, String> getLanguagesByIds() {
        return masterdataRepository.getLanguages(null, null).stream()
                .collect(Collectors.toMap(MasterdataValue::getId, MasterdataValue::getCode));
    }

    public Map<String, Long> getLanguagesByIdAndCode() {
        return masterdataRepository.getLanguages(null, null).stream()
                .collect(Collectors.toMap(MasterdataValue::getCode, MasterdataValue::getId));
    }

    public List<CodeDTO> getLanguagesByCode(Boolean platformLanguage) {
        return masterdataRepository.getLanguages(null, platformLanguage).stream()
                .map(language -> new CodeDTO(language.getCode())).collect(Collectors.toList());
    }

    public MasterdataValue getCountry(Long countryId) {
        return masterdataRepository.getCountry(countryId);
    }

    public MasterdataValue getCountrySubdivision(Long countrySubId) {
        return masterdataRepository.getCountrySubdivision(countrySubId);
    }

    public Integer getCountryIdByCode(String countryCode) {
        return getCountryByCode(countryCode).getId().intValue();
    }

    public Integer getCountrySubdivisionIdByCode(String countrySubdivisionCode) {
        return getCountrySubdivisionByCode(countrySubdivisionCode).getId().intValue();
    }

    public MasterdataValue getCountryByCode(String countryCode) {
        List<MasterdataValue> countries = masterdataRepository.getCountries(countryCode, null);
        if (countries.isEmpty()) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.NOT_FOUND)
                    .setMessage("country_code not found").build();
        }
        return countries.get(0);
    }

    public MasterdataValue getCountrySubdivisionByCode(String countrySubdivisionCode) {
        List<MasterdataValue> countries = masterdataRepository.getCountrySubdivisions(countrySubdivisionCode);
        if (countries.isEmpty()) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.NOT_FOUND)
                    .setMessage("country_subdivision_code not found").build();
        }
        return countries.get(0);
    }

    public TimeZone getTimezonesByOlson(String olson) {
        return masterdataRepository.getTimeZones().stream().filter(t -> t.getOlsonId().equals(olson))
                .findFirst().orElseThrow(() -> new OneboxRestException());
    }

    public List<Currency> getCurrencies() {
        return masterdataRepository.getCurrencies();
    }

    public List<IdNameDTO> getCustomerTypeTriggers() {
        return masterdataRepository.getCustomerTypeTriggers();
    }
    
    public List<ResourceServer> getAllResourceServers() {
        return masterdataRepository.getAllResourceServers();
    }
}
