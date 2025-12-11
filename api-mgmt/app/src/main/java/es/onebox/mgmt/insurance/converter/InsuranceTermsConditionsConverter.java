package es.onebox.mgmt.insurance.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsuranceTermsConditions;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsuranceTermsConditionsList;
import es.onebox.mgmt.datasources.ms.insurance.dto.UpdateInsuranceTermsConditions;
import es.onebox.mgmt.datasources.ms.insurance.dto.UpdateInsuranceTermsConditionsFileContent;
import es.onebox.mgmt.insurance.dto.InsuranceTermsConditionsDTO;
import es.onebox.mgmt.insurance.dto.InsuranceTermsConditionsListDTO;
import es.onebox.mgmt.insurance.dto.UpdateInsuranceTermsConditionsDTO;
import es.onebox.mgmt.insurance.dto.UpdateInsuranceTermsConditionsFileContentDTO;

public class InsuranceTermsConditionsConverter {

    private InsuranceTermsConditionsConverter() {}

    public static InsuranceTermsConditionsDTO toDto(InsuranceTermsConditions termsConditions) {
        if (termsConditions == null) {
            return null;
        }
        InsuranceTermsConditionsDTO target = new InsuranceTermsConditionsDTO();

        target.setId(termsConditions.getId());
        target.setInsurancePolicyId(termsConditions.getInsurancePolicyId());
        target.setLang(ConverterUtils.toLanguageTag(termsConditions.getLang()));
        target.setPrivacyPolicyText(termsConditions.getPrivacyPolicyText());
        target.setAgreementText(termsConditions.getAgreementText());
        target.setSubjectMailTemplate(termsConditions.getSubjectMailTemplate());
        target.setMailTemplate(termsConditions.getMailTemplate());
        target.setFile(termsConditions.getFile());
        target.setIsDefault(termsConditions.getIsDefault());

        return target;
    }

    public static InsuranceTermsConditionsListDTO toDtoList(InsuranceTermsConditionsList termsConditionsList) {
        InsuranceTermsConditionsListDTO response = new InsuranceTermsConditionsListDTO();
        if (termsConditionsList.getData() != null) {
            response.setData(termsConditionsList.getData().stream()
                    .map(InsuranceTermsConditionsConverter::toDto)
                    .toList());
        }
        response.setMetadata(termsConditionsList.getMetadata());
        return response;
    }

    public static UpdateInsuranceTermsConditions toEntity(UpdateInsuranceTermsConditionsDTO termsConditionsDTO) {
        if (termsConditionsDTO == null) {
            return null;
        }

        UpdateInsuranceTermsConditions target = new UpdateInsuranceTermsConditions();

        target.setPrivacyPolicyText(termsConditionsDTO.getPrivacyPolicyText());
        target.setAgreementText(termsConditionsDTO.getAgreementText());
        target.setSubjectMailTemplate(termsConditionsDTO.getSubjectMailTemplate());
        target.setMailTemplate(termsConditionsDTO.getMailTemplate());
        target.setIsDefault(termsConditionsDTO.getIsDefault());

        return target;
    }

    public static UpdateInsuranceTermsConditionsFileContent toEntity(UpdateInsuranceTermsConditionsFileContentDTO fileContentDTO) {
        if (fileContentDTO == null) {
            return null;
        }

        UpdateInsuranceTermsConditionsFileContent target = new UpdateInsuranceTermsConditionsFileContent();

        target.setFileName(fileContentDTO.getFileName());
        target.setFileContent(fileContentDTO.getFileContent());

        return target;
    }
}
