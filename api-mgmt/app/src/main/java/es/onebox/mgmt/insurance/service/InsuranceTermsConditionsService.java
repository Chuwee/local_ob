package es.onebox.mgmt.insurance.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.insurance.dto.UpdateInsuranceTermsConditions;
import es.onebox.mgmt.datasources.ms.insurance.dto.UpdateInsuranceTermsConditionsFileContent;
import es.onebox.mgmt.datasources.ms.insurance.repository.InsuranceTermsConditionsRepository;
import es.onebox.mgmt.exception.ApiMgmtInsuranceErrorCode;
import es.onebox.mgmt.insurance.converter.InsuranceTermsConditionsConverter;
import es.onebox.mgmt.insurance.dto.InsuranceTermsConditionsListDTO;
import es.onebox.mgmt.insurance.dto.UpdateInsuranceTermsConditionsDTO;
import es.onebox.mgmt.insurance.dto.UpdateInsuranceTermsConditionsFileContentDTO;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class InsuranceTermsConditionsService {
    private final InsuranceTermsConditionsRepository insuranceTermsConditionsRepository;

    public InsuranceTermsConditionsService(InsuranceTermsConditionsRepository insuranceTermsConditionsRepository) {
        this.insuranceTermsConditionsRepository = insuranceTermsConditionsRepository;
    }

    public InsuranceTermsConditionsListDTO getTermsConditionsList(Integer insurerId, Integer policyId, String lang) {
        return InsuranceTermsConditionsConverter.toDtoList(insuranceTermsConditionsRepository.getTermsConditionsListByPolicyId(insurerId, policyId, lang));
    }

    public void updateTermsConditions(Integer insurerId, Integer policyId, Integer termsId, UpdateInsuranceTermsConditionsDTO termsConditionsDTO) {
        UpdateInsuranceTermsConditions updateInsuranceTermsConditions = InsuranceTermsConditionsConverter.toEntity(termsConditionsDTO);

        insuranceTermsConditionsRepository.updateTermsConditions(insurerId, policyId, termsId, updateInsuranceTermsConditions);
    }

    public String getTermsConditionsFileContent(Integer insurerId, Integer policyId, Integer termsId) {
        return insuranceTermsConditionsRepository.getTermsConditionsFileContent(insurerId, policyId, termsId);
    }

    public void updateTermsConditionsFileContent(Integer insurerId, Integer policyId, Integer termsId, UpdateInsuranceTermsConditionsFileContentDTO fileContentDTO) {
        validateFileContentAndName(fileContentDTO);
        UpdateInsuranceTermsConditionsFileContent fileContent = InsuranceTermsConditionsConverter.toEntity(fileContentDTO);

        insuranceTermsConditionsRepository.updateTermsConditionsFileContent(insurerId, policyId, termsId, fileContent);
    }

    protected static void validateFileContentAndName(UpdateInsuranceTermsConditionsFileContentDTO updateFileContentDTO) {
        if (updateFileContentDTO.getFileContent() == null || updateFileContentDTO.getFileContent().trim().isEmpty()) {
            throw new OneboxRestException(ApiMgmtInsuranceErrorCode.INVALID_FILE_CONTENT);
        }
        if (updateFileContentDTO.getFileName() == null || updateFileContentDTO.getFileName().trim().isEmpty()) {
            throw new OneboxRestException(ApiMgmtInsuranceErrorCode.INVALID_FILE_NAME);
        }
        if (updateFileContentDTO.getFileName().contains(" ")) {
            throw new OneboxRestException(ApiMgmtInsuranceErrorCode.INVALID_FILE_NAME_SPACES);
        }
        Pattern fileNamePattern = Pattern.compile("^[a-zA-Z0-9._-]+\\.pdf$");
        if (!fileNamePattern.matcher(updateFileContentDTO.getFileName()).matches()) {
            throw new OneboxRestException(ApiMgmtInsuranceErrorCode.INVALID_FILE_NAME_CHARACTERS);
        }
    }
}
