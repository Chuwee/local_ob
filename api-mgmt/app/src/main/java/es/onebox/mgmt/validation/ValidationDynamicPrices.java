package es.onebox.mgmt.validation;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.LimitlessValueDTO;
import es.onebox.mgmt.common.LimitlessValueType;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.ConditionType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.sessions.dto.SessionPriceTypesAvailabilityDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.RequestDynamicPriceDTO;

import java.util.List;

public class ValidationDynamicPrices {

    public static void validateCapacityDynamicPrice(Long idPriceZone, List<RequestDynamicPriceDTO> requests, List<SessionPriceTypesAvailabilityDTO> availabilities) {
        if (CommonUtils.isEmpty(requests)) {
            return;
        }
        List<RequestDynamicPriceDTO> capacityDynamicPrices = requests.stream()
                .filter(dp -> dp.getConditionTypes() != null && dp.getConditionTypes().contains(ConditionType.CAPACITY))
                .toList();

        if (capacityDynamicPrices.isEmpty()) {
            return;
        }

        SessionPriceTypesAvailabilityDTO zoneAvailability = availabilities.stream()
                .filter(availability -> availability.getPriceType() != null &&
                        idPriceZone.equals(availability.getPriceType().getId()))
                .findFirst()
                .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.DYNAMIC_PRICE_ZONE_NOT_FOUND));

        LimitlessValueDTO totalCapacity = zoneAvailability.getAvailability() != null ?
                zoneAvailability.getAvailability().getTotal() : null;

        if (totalCapacity == null || LimitlessValueType.UNLIMITED.equals(totalCapacity.getType())) {
            return;
        }

        Long maxCapacity = totalCapacity.getValue();
        capacityDynamicPrices.forEach(capacityDynamicPrice -> {
            if (capacityDynamicPrice.getCapacity() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.MISSING_CAPACITY);
            }
            if (capacityDynamicPrice.getCapacity() != null &&
                    capacityDynamicPrice.getCapacity() > maxCapacity) {
                throw new OneboxRestException(ApiMgmtErrorCode.DYNAMIC_PRICE_CAPACITY_EXCEEDED);
            }
            if (capacityDynamicPrice.getCapacity() <= 0) {
                throw new OneboxRestException(ApiMgmtErrorCode.CAPACITY_DYNAMIC_PRICE_CANNOT_BE_ZERO_OR_NEGATIVE);
            }
        });
    }
}
