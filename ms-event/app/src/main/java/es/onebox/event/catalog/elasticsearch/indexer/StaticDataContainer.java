package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.event.events.dao.AdmissionAgeDao;
import es.onebox.event.events.dao.CountryDao;
import es.onebox.event.events.dao.TagCommunicationElementDao;
import es.onebox.event.language.dao.LanguageDao;
import es.onebox.event.taxonomy.dao.BaseTaxonomyDao;
import es.onebox.event.timezone.dao.TimeZoneDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCalificacionEdadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPaisRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTagElementosComRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTaxonomiaBaseRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTimeZoneGroupRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Component
public class StaticDataContainer {

    private final Map<Integer, IdNameCodeDTO> countries;
    private final Map<Integer, String> timeZones;
    private final Map<String, String> admissions;
    private final Map<Integer, TagInfo> tagInfo;
    private final Map<Integer, String> languages;
    private final Map<String, Integer> languageCodes;
    private final Map<Integer, BaseTaxonomyDao.TaxonomyInfo> baseTaxonomies;
    private final String s3Repository;

    @Autowired
    public StaticDataContainer(CountryDao countryDao,
                               TimeZoneDao timeZoneDao,
                               LanguageDao languageDao,
                               AdmissionAgeDao admissionAgeDao,
                               TagCommunicationElementDao tagCommunicationElementDao,
                               BaseTaxonomyDao baseTaxonomyDao,
                               @Value("${onebox.repository.S3SecureUrl}") String s3domain,
                               @Value("${onebox.repository.fileBasePath}") String fileBasePath) {

        countries = countryDao.getAll().stream()
                .collect(toMap(CpanelPaisRecord::getIdpais,
                        c -> new IdNameCodeDTO(c.getIdpais().longValue(), c.getNombre(), c.getCodigo())));

        timeZones = timeZoneDao.getAll().stream()
                .collect(toMap(CpanelTimeZoneGroupRecord::getZoneid, CpanelTimeZoneGroupRecord::getOlsonid));

        List<CpanelIdiomaRecord> allLanguages = languageDao.getAll();
        languages = allLanguages.stream()
                .collect(toMap(CpanelIdiomaRecord::getIdidioma, CpanelIdiomaRecord::getCodigo));
        languageCodes = allLanguages.stream()
                .collect(toMap(CpanelIdiomaRecord::getCodigo, CpanelIdiomaRecord::getIdidioma));

        admissions = admissionAgeDao.getAll().stream()
                .collect(toMap(CpanelCalificacionEdadRecord::getIdcalificacionedad, CpanelCalificacionEdadRecord::getDescripcion));

        tagInfo = tagCommunicationElementDao.getAll().stream()
                .collect(toMap(CpanelTagElementosComRecord::getIdtag,
                        t -> new TagInfo(t.getTag(), t.getIdtag(), t.getIdtipoelemento(), t.getOrden())));

        baseTaxonomies = baseTaxonomyDao.getAll().stream()
                .collect(toMap(CpanelTaxonomiaBaseRecord::getIdtaxonomia,
                        t -> new BaseTaxonomyDao.TaxonomyInfo(t.getIdtaxonomia(), t.getIdtaxonomiasuperior(), t.getCodigo(), t.getDescripcion())));

        s3Repository = s3domain + fileBasePath;
    }

    public IdNameCodeDTO getCountry(Integer countryId) {
        return countries.get(countryId);
    }

    public String getTimeZone(Integer zoneId) {
        return timeZones.get(zoneId);
    }

    public String getLanguage(Integer languageId) {
        return languages.get(languageId);
    }

    public Integer getLanguageByCode(String languageCode) {
        return languageCodes.get(languageCode);
    }

    public String getAdmission(String admissionId) {
        return admissions.get(admissionId);
    }

    public String getTag(Integer tagId) {
        return tagInfo.get(tagId) != null ? tagInfo.get(tagId).tag() : null;
    }

    public Integer getTagId(Integer tagId) {
        return tagInfo.get(tagId) != null ? tagInfo.get(tagId).tagId() : null;
    }

    public Integer getOrder(Integer tagId) {
        return tagInfo.get(tagId) != null ? tagInfo.get(tagId).order() : null;
    }

    public BaseTaxonomyDao.TaxonomyInfo getBaseTaxonomy(Integer taxonomyId) {
        return baseTaxonomies.get(taxonomyId);
    }

    public String getS3Repository() {
        return s3Repository;
    }

    private record TagInfo(
            String tag,
            Integer tagId,
            Integer elementTypeId,
            Integer order) {
    }

}
