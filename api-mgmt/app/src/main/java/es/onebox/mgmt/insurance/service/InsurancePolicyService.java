package es.onebox.mgmt.insurance.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicies;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicyV1;
import es.onebox.mgmt.datasources.ms.insurance.dto.UpdateInsurancePolicy;
import es.onebox.mgmt.datasources.ms.insurance.repository.InsurancePoliciesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.insurance.converter.InsurancePolicyConverter;
import es.onebox.mgmt.insurance.dto.InsurancePoliciesDTO;
import es.onebox.mgmt.insurance.dto.InsurancePolicyCreateDTO;
import es.onebox.mgmt.insurance.dto.InsurancePolicyDTO;
import es.onebox.mgmt.insurance.dto.UpdateInsurancePolicyDTO;
import es.onebox.mgmt.insurance.enums.PolicyState;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.stereotype.Service;

@Service
public class InsurancePolicyService {

    private final InsurancePoliciesRepository insurancePoliciesRepository;
    private final MasterdataService masterdataService;
    private final ValidationService validationService;
    private final EntitiesRepository entitiesRepository;

    public InsurancePolicyService(InsurancePoliciesRepository insurancePoliciesRepository,
                                  MasterdataService masterdataService, ValidationService validationService,
                                  EntitiesRepository entitiesRepository) {
        this.insurancePoliciesRepository = insurancePoliciesRepository;
        this.masterdataService = masterdataService;
        this.validationService = validationService;
        this.entitiesRepository = entitiesRepository;
    }

    public InsurancePoliciesDTO getPoliciesByInsurerId(Integer insurerId) {
        InsurancePolicies policies = insurancePoliciesRepository.getPoliciesByInsurerId(insurerId);

        if (policies.getData() != null) {
            policies.setData(
                    policies.getData().stream()
                            .filter(p -> !PolicyState.DELETED.equals(p.getState()))
                            .toList()
            );
        }

        return InsurancePolicyConverter.toDtoList(policies);
    }

    public InsurancePolicyDTO getPolicyDetails(Integer insurerId,
                                               Integer policyId) {
        return InsurancePolicyConverter.toDto(insurancePoliciesRepository.getPolicyDetails(insurerId, policyId), masterdataService);
    }

    public InsurancePolicyDTO createPolicy(Integer insurerId, InsurancePolicyCreateDTO insurancePolicyCreateDTO) {
        validateFieldsPolicyDTO(insurancePolicyCreateDTO);

        var insurer = validationService.getAndCheckInsurer(insurerId);
        Integer operatorId = insurer.getOperatorId();
        
        var operator = entitiesRepository.getOperator(operatorId.longValue());
        
        var languages = InsurancePolicyConverter.convertEntityLanguagesToPolicyLanguages(
            operator.getLanguage(), 
            operator.getSelectedLanguages()
        );

        InsurancePolicyV1 insurancePolicy = InsurancePolicyConverter.toEntity(insurancePolicyCreateDTO, languages);
        InsurancePolicyV1 response = insurancePoliciesRepository.createPolicy(insurerId, insurancePolicy);

        return InsurancePolicyConverter.toDto(response, masterdataService);
    }

    public void updatePolicy(Integer insurerId, Integer policyId, UpdateInsurancePolicyDTO updateInsurancePolicyDTO) {
        UpdateInsurancePolicy updateInsurancePolicy = InsurancePolicyConverter.toEntity(updateInsurancePolicyDTO, masterdataService);

        insurancePoliciesRepository.updatePolicy(insurerId, policyId, updateInsurancePolicy);
    }

    private void validateFieldsPolicyDTO(InsurancePolicyCreateDTO dto) {
        if (dto.getName() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Policy has to contain a name", null);
        } else if (dto.getName().length() > 50) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Policy name has limit of 50 chars", null);
        }

        if (dto.getPolicyNumber() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Policy has to contain a policy number", null);
        } else if (dto.getPolicyNumber().length() > 50) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Policy number has limit of 50 chars", null);
        }

        if (dto.getTaxes() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Policy has to contain a taxes", null);
        }
    }

    public void deletePolicy(Integer insurerId, Integer policyId) {
        validationService.getAndCheckPolicy(insurerId, policyId);
        insurancePoliciesRepository.deletePolicy(insurerId, policyId);
    }
}
