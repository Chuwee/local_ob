package es.onebox.mgmt.insurance.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicyLanguage;
import es.onebox.mgmt.events.dto.LanguagesDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PolicyLanguageConverter {

    private PolicyLanguageConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static LanguagesDTO convertToLanguagesDTO(List<InsurancePolicyLanguage> languageItems, MasterdataService masterdataService) {
        if (CommonUtils.isEmpty(languageItems)) {
            return null;
        }

        Map<Long, String> languagesMap = masterdataService.getLanguagesByIds();

        String defaultLanguage = languageItems.stream()
                .filter(item -> Objects.nonNull(item.getIsDefault()) && item.getIsDefault())
                .map(InsurancePolicyLanguage::getId)
                .filter(Objects::nonNull)
                .map(id -> languagesMap.get(id.longValue()))
                .filter(Objects::nonNull)
                .map(PolicyLanguageConverter::denormalizeLanguageCode)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (defaultLanguage == null && !languageItems.isEmpty()) {
            defaultLanguage = languageItems.stream()
                    .map(InsurancePolicyLanguage::getId)
                    .filter(Objects::nonNull)
                    .map(id -> languagesMap.get(id.longValue()))
                    .filter(Objects::nonNull)
                    .map(PolicyLanguageConverter::denormalizeLanguageCode)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }

        List<String> selectedLanguages = languageItems.stream()
                .map(InsurancePolicyLanguage::getId)
                .filter(Objects::nonNull)
                .map(id -> languagesMap.get(id.longValue()))
                .filter(Objects::nonNull)
                .map(PolicyLanguageConverter::denormalizeLanguageCode)
                .filter(Objects::nonNull)
                .toList();

        LanguagesDTO languagesDTO = new LanguagesDTO();
        languagesDTO.setDefaultLanguage(defaultLanguage);
        languagesDTO.setSelected(selectedLanguages);

        return languagesDTO;
    }

    public static List<InsurancePolicyLanguage> fromDto(LanguagesDTO languagesDTO, MasterdataService masterdataService) {
        if (languagesDTO == null) {
            return Collections.emptyList();
        }

        LanguagesDTO normalizedLanguagesDTO = normalizeLanguageCodes(languagesDTO);

        return convertFromLanguagesDTO(normalizedLanguagesDTO, masterdataService);
    }

    private static List<InsurancePolicyLanguage> convertFromLanguagesDTO(LanguagesDTO languagesDTO, MasterdataService masterdataService) {
        if (languagesDTO == null || CommonUtils.isEmpty(languagesDTO.getSelected())) {
            return Collections.emptyList();
        }

        Map<String, Long> languageCodeToIdMap = masterdataService.getLanguagesByIdAndCode();

        return languagesDTO.getSelected().stream()
                .filter(languageCodeToIdMap::containsKey)
                .map(languageCode -> {
                    InsurancePolicyLanguage language = new InsurancePolicyLanguage();
                    language.setId(languageCodeToIdMap.get(languageCode).intValue());
                    language.setIsDefault(Objects.equals(languageCode, languagesDTO.getDefaultLanguage()));
                    return language;
                })
                .toList();
    }

    private static LanguagesDTO normalizeLanguageCodes(LanguagesDTO languagesDTO) {
        if (languagesDTO == null) {
            return null;
        }

        LanguagesDTO normalized = new LanguagesDTO();

        if (languagesDTO.getDefaultLanguage() != null) {
            normalized.setDefaultLanguage(normalizeLanguageCode(languagesDTO.getDefaultLanguage()));
        }

        if (languagesDTO.getSelected() != null) {
            List<String> normalizedSelected = languagesDTO.getSelected().stream()
                    .map(PolicyLanguageConverter::normalizeLanguageCode)
                    .toList();
            normalized.setSelected(normalizedSelected);
        }

        return normalized;
    }

    private static String normalizeLanguageCode(String languageCode) {
        if (languageCode == null) {
            return null;
        }
        return languageCode.replace('-', '_');
    }

    private static String denormalizeLanguageCode(String languageCode) {
        if (languageCode == null) {
            return null;
        }
        return languageCode.replace('_', '-');
    }
}

