package es.onebox.mgmt.common.surcharges;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.surcharges.dto.CommonSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeLimitDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonSurchargeService {

    public void validateSurcharges(List<? extends CommonSurchargeDTO> commonSurchargeDTOS) {
        if(commonSurchargeDTOS.stream().anyMatch(surchargeDTO -> surchargeDTO.getType() == null)) {
            throw new OneboxRestException(ApiMgmtErrorCode.TYPE_MANDATORY);
        }

        if(commonSurchargeDTOS.stream().anyMatch(this::hasFixedAndPercentageNull)) {
            throw new OneboxRestException(ApiMgmtErrorCode.FIXED_OR_PERCENTAGE_MANDATORY);
        }

        if(hasTypesDuplicated(commonSurchargeDTOS)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SURCHARGE_TYPE_DUPLICATED);
        }

        if(commonSurchargeDTOS.stream().anyMatch(surcharge ->
            SurchargeTypeDTO.SECONDARY_MARKET_PROMOTER.equals(surcharge.getType()) && hasSurchargeLimits(surcharge.getLimit()))) {
                throw new OneboxRestException(ApiMgmtErrorCode.SECONDARY_MARKET_CANT_HAVE_LIMIT);
        }
    }

    private static boolean hasTypesDuplicated(List<? extends CommonSurchargeDTO> commonSurchargeDTOS) {
        return commonSurchargeDTOS.stream()
                .anyMatch(surchargeDTO ->
                        commonSurchargeDTOS.stream()
                                .filter(singleSurcharge -> singleSurcharge.getType().equals(surchargeDTO.getType()))
                                .count() > 1);
    }

    private boolean hasFixedAndPercentageNull(SurchargeDTO surchargeDTO) {
        if (CollectionUtils.isNotEmpty(surchargeDTO.getRanges())) {
            return surchargeDTO.getRanges().stream()
                    .anyMatch(rangeDTO -> rangeDTO.getValues().getFixed() == null && rangeDTO.getValues().getPercentage() == null);
        }
        throw new OneboxRestException(ApiMgmtErrorCode.SURCHARGE_RANGE_MANDATORY);
    }

    private boolean hasSurchargeLimits(SurchargeLimitDTO limitDTO) {
        return limitDTO != null && ( CommonUtils.isTrue(limitDTO.getEnabled())
                                    || limitDTO.getMin() != null
                                    || limitDTO.getMax() != null);
    }
}