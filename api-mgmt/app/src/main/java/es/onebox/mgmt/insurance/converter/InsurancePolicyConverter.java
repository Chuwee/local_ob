package es.onebox.mgmt.insurance.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicies;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicyBasic;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicyLanguage;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicyV1;
import es.onebox.mgmt.datasources.ms.insurance.dto.UpdateInsurancePolicy;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.insurance.dto.InsurancePoliciesDTO;
import es.onebox.mgmt.insurance.dto.InsurancePolicyBasicDTO;
import es.onebox.mgmt.insurance.dto.InsurancePolicyCreateDTO;
import es.onebox.mgmt.insurance.dto.InsurancePolicyDTO;
import es.onebox.mgmt.insurance.dto.UpdateInsurancePolicyDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InsurancePolicyConverter {

    private InsurancePolicyConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static InsurancePolicyDTO toDto(InsurancePolicyV1 insurancePolicy) {
        if (insurancePolicy == null) {
            return null;
        }
        InsurancePolicyDTO target = new InsurancePolicyDTO();

        target.setId(insurancePolicy.getId());
        target.setInsurerId(insurancePolicy.getInsurerId());
        target.setName(insurancePolicy.getName());
        target.setPolicyNumber(insurancePolicy.getPolicyNumber());
        target.setDescription(insurancePolicy.getDescription());
        target.setDaysAheadLimit(insurancePolicy.getDaysAheadLimit());
        target.setPolicyState(insurancePolicy.getState());
        target.setTaxes(insurancePolicy.getTaxes());
        target.setInsurerBenefitsFix(insurancePolicy.getInsurerBenefitsFix());
        target.setInsurerBenefitsPercent(insurancePolicy.getInsurerBenefitsPercent());
        target.setOperatorBenefitsFix(insurancePolicy.getOperatorBenefitsFix());
        target.setOperatorBenefitsPercent(insurancePolicy.getOperatorBenefitsPercent());
        target.setDefaultAllowed(insurancePolicy.getDefaultAllowed());

        return target;
    }

    public static InsurancePolicyDTO toDto(InsurancePolicyV1 insurancePolicy, MasterdataService masterdataService) {
        if (insurancePolicy == null) {
            return null;
        }
        
        InsurancePolicyDTO target = toDto(insurancePolicy);
        
        if (masterdataService != null && insurancePolicy.getLanguages() != null) {
            target.setLanguages(PolicyLanguageConverter.convertToLanguagesDTO(insurancePolicy.getLanguages(), masterdataService));
        }
        
        return target;
    }

    public static InsurancePolicyBasicDTO toBasicDto(InsurancePolicyBasic insurancePolicyBasic) {
        if (insurancePolicyBasic == null) {
            return null;
        }
        InsurancePolicyBasicDTO target = new InsurancePolicyBasicDTO();

        target.setId(insurancePolicyBasic.getId());
        target.setInsurerId(insurancePolicyBasic.getInsurerId());
        target.setName(insurancePolicyBasic.getName());
        target.setPolicyNumber(insurancePolicyBasic.getPolicyNumber());
        target.setPolicyState(insurancePolicyBasic.getState());

        return target;
    }

    public static InsurancePoliciesDTO toDtoList(InsurancePolicies insurancePolicies) {
        InsurancePoliciesDTO response = new InsurancePoliciesDTO();
        if (insurancePolicies.getData() != null) {
            response.setData(insurancePolicies.getData().stream()
                    .map(InsurancePolicyConverter::toBasicDto)
                    .toList());
        }
        response.setMetadata(insurancePolicies.getMetadata());
        return response;
    }

    public static UpdateInsurancePolicy toEntity(UpdateInsurancePolicyDTO updateInsurancePolicyDTO, MasterdataService masterdataService) {
        if (updateInsurancePolicyDTO == null) {
            return null;
        }

        UpdateInsurancePolicy target = new UpdateInsurancePolicy();

        target.setName(updateInsurancePolicyDTO.getName());
        target.setActive(updateInsurancePolicyDTO.getActive());
        target.setDescription(updateInsurancePolicyDTO.getDescription());
        target.setDaysAheadLimit(updateInsurancePolicyDTO.getDaysAheadLimit());
        target.setTaxes(updateInsurancePolicyDTO.getTaxes());
        target.setInsurerBenefitsFix(updateInsurancePolicyDTO.getInsurerBenefitsFix());
        target.setInsurerBenefitsPercent(updateInsurancePolicyDTO.getInsurerBenefitsPercent());
        target.setOperatorBenefitsFix(updateInsurancePolicyDTO.getOperatorBenefitsFix());
        target.setOperatorBenefitsPercent(updateInsurancePolicyDTO.getOperatorBenefitsPercent());
        target.setExternalProvider(updateInsurancePolicyDTO.getExternalProvider());
        target.setDefaultAllowed(updateInsurancePolicyDTO.getDefaultAllowed());
        
        if (masterdataService != null && updateInsurancePolicyDTO.getLanguages() != null) {
            target.setLanguages(PolicyLanguageConverter.fromDto(updateInsurancePolicyDTO.getLanguages(), masterdataService));
        }

        return target;
    }


    public static InsurancePolicyV1 toEntity(InsurancePolicyDTO insurancePolicyDTO) {
        if (insurancePolicyDTO == null) {
            return null;
        }

        InsurancePolicyV1 target = new InsurancePolicyV1();

        target.setName(insurancePolicyDTO.getName());
        target.setPolicyNumber(insurancePolicyDTO.getPolicyNumber());
        target.setState(insurancePolicyDTO.getPolicyState());
        target.setDescription(insurancePolicyDTO.getDescription());
        target.setDaysAheadLimit(insurancePolicyDTO.getDaysAheadLimit());
        target.setTaxes(insurancePolicyDTO.getTaxes());
        target.setInsurerBenefitsFix(insurancePolicyDTO.getInsurerBenefitsFix());
        target.setInsurerBenefitsPercent(insurancePolicyDTO.getInsurerBenefitsPercent());
        target.setOperatorBenefitsFix(insurancePolicyDTO.getOperatorBenefitsFix());
        target.setOperatorBenefitsPercent(insurancePolicyDTO.getOperatorBenefitsPercent());

        return target;
    }

    public static InsurancePolicyV1 toEntity(InsurancePolicyCreateDTO dto) {
        return toEntity(dto, null);
    }

    public static InsurancePolicyV1 toEntity(InsurancePolicyCreateDTO dto, List<InsurancePolicyLanguage> languages) {
        if (dto == null) {
            return null;
        }

        InsurancePolicyV1 target = new InsurancePolicyV1();
        target.setName(dto.getName());
        target.setPolicyNumber(dto.getPolicyNumber());
        target.setTaxes(dto.getTaxes());
        
        if (languages != null && !languages.isEmpty()) {
            target.setLanguages(languages);
        }

        return target;
    }

    public static List<InsurancePolicyLanguage> convertEntityLanguagesToPolicyLanguages(
            IdValueCodeDTO defaultLanguage, List<IdValueCodeDTO> selectedLanguages) {
        
        validateLanguagesInput(defaultLanguage, selectedLanguages);
        
        IdValueCodeDTO finalDefaultLanguage = determineDefaultLanguage(defaultLanguage, selectedLanguages);
        Long defaultLanguageId = finalDefaultLanguage.getId();
        
        List<IdValueCodeDTO> languagesToProcess = buildLanguagesList(finalDefaultLanguage, selectedLanguages, defaultLanguageId);
        
        return convertToInsurancePolicyLanguages(languagesToProcess, defaultLanguageId);
    }

    private static void validateLanguagesInput(IdValueCodeDTO defaultLanguage, List<IdValueCodeDTO> selectedLanguages) {
        boolean hasDefault = hasValidDefaultLanguage(defaultLanguage);
        boolean hasSelected = hasValidSelectedLanguages(selectedLanguages);
        
        if (!hasDefault && !hasSelected) {
            throw new OneboxRestException(
                ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, 
                "Entity must have at least default language or selected languages configured", 
                null
            );
        }
    }

    private static boolean hasValidDefaultLanguage(IdValueCodeDTO defaultLanguage) {
        return defaultLanguage != null && defaultLanguage.getId() != null;
    }

    private static boolean hasValidSelectedLanguages(List<IdValueCodeDTO> selectedLanguages) {
        return !CommonUtils.isEmpty(selectedLanguages);
    }

    private static IdValueCodeDTO determineDefaultLanguage(
            IdValueCodeDTO defaultLanguage, List<IdValueCodeDTO> selectedLanguages) {
        
        if (hasValidDefaultLanguage(defaultLanguage)) {
            return defaultLanguage;
        }
        
        return extractFirstValidLanguage(selectedLanguages);
    }

    private static IdValueCodeDTO extractFirstValidLanguage(List<IdValueCodeDTO> selectedLanguages) {
        return selectedLanguages.stream()
                .filter(Objects::nonNull)
                .filter(lang -> lang.getId() != null)
                .findFirst()
                .orElseThrow(() -> new OneboxRestException(
                    ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                    "Selected languages list is empty or contains invalid entries",
                    null
                ));
    }

    private static List<IdValueCodeDTO> buildLanguagesList(
            IdValueCodeDTO defaultLanguage, 
            List<IdValueCodeDTO> selectedLanguages, 
            Long defaultLanguageId) {
        
        List<IdValueCodeDTO> languagesToProcess = new ArrayList<>();
        languagesToProcess.add(defaultLanguage);
        
        if (hasValidSelectedLanguages(selectedLanguages)) {
            addSelectedLanguagesWithoutDuplicates(languagesToProcess, selectedLanguages, defaultLanguageId);
        }
        
        return languagesToProcess;
    }

    private static void addSelectedLanguagesWithoutDuplicates(
            List<IdValueCodeDTO> languagesToProcess,
            List<IdValueCodeDTO> selectedLanguages,
            Long defaultLanguageId) {
        
        for (IdValueCodeDTO selectedLang : selectedLanguages) {
            if (isValidLanguage(selectedLang) && !isDuplicate(selectedLang, defaultLanguageId)) {
                languagesToProcess.add(selectedLang);
            }
        }
    }

    private static boolean isValidLanguage(IdValueCodeDTO language) {
        return language != null && language.getId() != null;
    }

    private static boolean isDuplicate(IdValueCodeDTO language, Long defaultLanguageId) {
        return defaultLanguageId.equals(language.getId());
    }

    private static List<InsurancePolicyLanguage> convertToInsurancePolicyLanguages(
            List<IdValueCodeDTO> languagesToProcess, 
            Long defaultLanguageId) {
        
        return languagesToProcess.stream()
                .filter(Objects::nonNull)
                .filter(lang -> lang.getId() != null)
                .map(lang -> createInsurancePolicyLanguage(lang, defaultLanguageId))
                .toList();
    }

    private static InsurancePolicyLanguage createInsurancePolicyLanguage(
            IdValueCodeDTO language, 
            Long defaultLanguageId) {
        
        InsurancePolicyLanguage policyLanguage = new InsurancePolicyLanguage();
        policyLanguage.setId(language.getId().intValue());
        policyLanguage.setIsDefault(defaultLanguageId.equals(language.getId()));
        return policyLanguage;
    }
}
