package es.onebox.mgmt.insurance.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsuranceRange;
import es.onebox.mgmt.datasources.ms.insurance.repository.InsuranceRangesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.insurance.converter.InsuranceRangeConverter;
import es.onebox.mgmt.insurance.dto.InsuranceRangeDTO;
import es.onebox.mgmt.insurance.dto.InsuranceRangeDTOList;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsuranceRangeService {

    private final InsuranceRangesRepository insuranceRangesRepository;

    public InsuranceRangeService(InsuranceRangesRepository insuranceRangesRepository) {
        this.insuranceRangesRepository = insuranceRangesRepository;
    }

    public List<InsuranceRangeDTO> getRangesByPolicyId(Integer insurerId, Integer policyId) {
        List<InsuranceRange> insuranceRanges = insuranceRangesRepository.getRangesByPolicyId(insurerId, policyId);
        return InsuranceRangeConverter.toDTOs(insuranceRanges);
    }

    public List<InsuranceRangeDTO> updateRangesByPolicyId(
            Integer insurerId, Integer policyId, InsuranceRangeDTOList insuranceRangeDTOList) {

        validateFieldsOfInsuranceRangeDTOList(insuranceRangeDTOList);

        List<InsuranceRange> insuranceRangesRequest = InsuranceRangeConverter.toEntities(insuranceRangeDTOList);
        List<InsuranceRange> insuranceRanges =
                insuranceRangesRepository.updateRangesByPolicyId(insurerId, policyId, insuranceRangesRequest);

        return InsuranceRangeConverter.toDTOs(insuranceRanges);
    }

    private void validateFieldsOfInsuranceRangeDTOList(List<InsuranceRangeDTO> rangeDTOList) {

        rangeDTOList.forEach(this::validateFieldsOfRangeDTO);

        if (rangeDTOList.stream().noneMatch(rangeDTO -> rangeDTO.getFrom() == 0.0)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "It has to be present a Min and a Max values equals a 0.0 ", null);
        }
    }

    private void validateFieldsOfRangeDTO(InsuranceRangeDTO rangeDTO) {

        if (rangeDTO.getFrom() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Range has to contain a From value", null);
        }

        if (rangeDTO.getValues().getFixed() == null && rangeDTO.getValues().getPercentage() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Range has to contain a Charge Fix or a Charge Percent value", null);
        }

        if (rangeDTO.getValues().getFixed() != null && rangeDTO.getValues().getPercentage() != null) {
            throw new OneboxRestException(ApiMgmtErrorCode.FIX_OR_PERCENTAGE_ERROR, "Range has to contain or a Charge Fix or a Charge Percent value only", null);
        }
    }
}
