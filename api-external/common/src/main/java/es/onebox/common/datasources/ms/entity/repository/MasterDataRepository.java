package es.onebox.common.datasources.ms.entity.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.ms.entity.MsEntityDatasource;
import es.onebox.common.datasources.ms.entity.dto.CountryDTO;
import es.onebox.common.datasources.ms.entity.dto.CountrySubdivisionDTO;
import es.onebox.common.datasources.ms.entity.dto.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class MasterDataRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public MasterDataRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    @Cached(key = "MasterDataRepository_getLanguage", expires = 5 * 60)
    public Language getLanguage(@CachedArg Long languageId) {
        return msEntityDatasource.getLanguage(languageId);
    }

    @Cached(key = "MasterDataRepository_findLanguages", expires = 5 * 60)
    public List<Language> findLanguages(@CachedArg String langCode) {
        return msEntityDatasource.findLanguages(langCode);
    }

    @Cached(key = "MasterDataRepository_countries", expires = 60 * 60)
    public List<CountryDTO> countries() {
        return msEntityDatasource.getCountries();
    }

    @Cached(key = "MasterDataRepository_subdivisions", expires = 60 * 60)
    public Map<String, CountrySubdivisionDTO> getSubdivisionsByCountryCode() {
        List<CountrySubdivisionDTO> subdivisions = msEntityDatasource.getCountrySubdivisions();
        return subdivisions.stream()
                .filter(s -> !s.getCode().equals("NOT_DEF"))
                .collect(Collectors.toMap(CountrySubdivisionDTO::getCode, Function.identity(), (v1, v2) -> v1));
    }
}
