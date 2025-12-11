package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.common.dto.TimeZone;
import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.CountryWithTaxCalculationDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataCountryValue;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.ResourceServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MasterdataRepository {

    private static final String CACHE_MASTERDATA_LANGUAGE_KEY = "masterdata.language";
    private static final String CACHE_MASTERDATA_LANGUAGE_BY_CODE_KEY = "masterdata.language.code";
    private static final String CACHE_MASTERDATA_TIMEZONES_KEY = "masterdata.timezones";
    private static final String CACHE_MASTERDATA_CURRENCIES = "masterdata.currencies";
    private static final String CACHE_MASTERDATA_COUNTRIES_KEY = "masterdata.countries";
    private static final String CACHE_MASTERDATA_COUNTRY_KEY = "masterdata.country";
    private static final String CACHE_MASTERDATA_COUNTRY_COUNTRY_SUB_KEY = "masterdata.country.countrysub";
    private static final String CACHE_MASTERDATA_COUNTRY_SUB_KEY = "masterdata.countrysub";
    private static final String CACHE_MASTERDATA_RESOURCE_SERVER_KEY = "masterdata.resourceserver";
    private static final String CACHE_MASTERDATA_INTERNATIONAL_PHONE_PREFIX = "masterdata.countries.internationalphoneprefixes";
    private static final String CACHE_MASTERDATA_CUSTOMER_TYPE_TRIGGERS = "masterdata.customertype.triggers";
    private static final int CACHE_MASTERDATA_TTL = 600;

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public MasterdataRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    @Cached(key = CACHE_MASTERDATA_LANGUAGE_KEY, expires = CACHE_MASTERDATA_TTL)
    public MasterdataValue getLanguage(@CachedArg Long languageId) {
        return msEntityDatasource.getLanguage(languageId);
    }

    @Cached(key = CACHE_MASTERDATA_LANGUAGE_BY_CODE_KEY, expires = CACHE_MASTERDATA_TTL)
    public List<MasterdataValue> getLanguages(@CachedArg String languageCode, @CachedArg Boolean platformLanguage) {
        return msEntityDatasource.getLanguages(languageCode, platformLanguage);
    }

    @Cached(key = CACHE_MASTERDATA_COUNTRY_KEY, expires = CACHE_MASTERDATA_TTL)
    public MasterdataValue getCountry(@CachedArg Long countryId) {
        return msEntityDatasource.getCountry(countryId);
    }

    @Cached(key = CACHE_MASTERDATA_COUNTRIES_KEY, expires = CACHE_MASTERDATA_TTL)
    public List<MasterdataValue> getCountries(@CachedArg String countryCode, @CachedArg Boolean systemCountry) {
        return msEntityDatasource.getCountries(countryCode, systemCountry);
    }

    @Cached(key = CACHE_MASTERDATA_COUNTRIES_KEY + ".withTaxCalculation", expires = CACHE_MASTERDATA_TTL)
    public List<CountryWithTaxCalculationDTO> getCountriesWithTaxCalculation(@CachedArg String countryCode, @CachedArg Boolean systemCountry) {
        return msEntityDatasource.getCountriesWithTaxCalculation(countryCode, systemCountry);
    }

    @Cached(key = CACHE_MASTERDATA_COUNTRY_COUNTRY_SUB_KEY, expires = CACHE_MASTERDATA_TTL)
    public List<MasterdataValue> getCountrySubdivisionByCountryId(@CachedArg Long countryId) {
        return msEntityDatasource.getCountrySubdivisionByCountryId(countryId);
    }

    @Cached(key = CACHE_MASTERDATA_COUNTRY_SUB_KEY, expires = CACHE_MASTERDATA_TTL)
    public MasterdataValue getCountrySubdivision(@CachedArg Long countrySubdivisionId) {
        return msEntityDatasource.getCountrySubdivision(countrySubdivisionId);
    }

    @Cached(key = CACHE_MASTERDATA_LANGUAGE_BY_CODE_KEY, expires = CACHE_MASTERDATA_TTL)
    public List<MasterdataValue> getCountrySubdivisions(@CachedArg String countrySubdivisionCode) {
        return msEntityDatasource.getCountrySubdivisions(countrySubdivisionCode);
    }

    @Cached(key = CACHE_MASTERDATA_TIMEZONES_KEY, expires = CACHE_MASTERDATA_TTL)
    public List<TimeZone> getTimeZones() {
        return msEntityDatasource.getTimeZones();
    }

    @Cached(key = CACHE_MASTERDATA_CURRENCIES, expires = CACHE_MASTERDATA_TTL)
    public List<Currency> getCurrencies() {
        return msEntityDatasource.getCurrencies();
    }

    @Cached(key= CACHE_MASTERDATA_INTERNATIONAL_PHONE_PREFIX, expires = CACHE_MASTERDATA_TTL)
    public List<MasterdataCountryValue> getAllInternationalPhonePrefixes() {
        return msEntityDatasource.getAllInternationalPhonePrefixes();
    }
    
    @Cached(key= CACHE_MASTERDATA_RESOURCE_SERVER_KEY, expires = CACHE_MASTERDATA_TTL)
    public List<ResourceServer> getAllResourceServers() {
        return msEntityDatasource.getAllResourceServers();
    }

    @Cached(key = CACHE_MASTERDATA_CUSTOMER_TYPE_TRIGGERS, expires = CACHE_MASTERDATA_TTL)
    public List<IdNameDTO> getCustomerTypeTriggers() {
        return msEntityDatasource.getCustomerTriggers();
    }
}
