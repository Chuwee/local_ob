package es.onebox.mgmt.validation;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import es.onebox.mgmt.events.dto.RateRestrictionDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

@Component
public class RateRestrictionsValidator {


    public static void validateRestrictions(Event event, RateRestrictionDTO rateRestrictionDTO) {
        if (rateRestrictionDTO == null) {
            return;
        }

        if (EventType.AVET.equals(event.getType())) {
            if (BooleanUtils.isTrue(rateRestrictionDTO.getDateRestrictionEnabled())
                    || BooleanUtils.isTrue(rateRestrictionDTO.getCustomerTypeRestrictionEnabled())) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_RATE_RESTRICTIONS);
            }
        }
        if (rateRestrictionDTO.getRateRelationsRestriction() != null) {
            if (rateRestrictionDTO.getRateRelationsRestriction().getRequiredTicketsNumber() != null
                    && rateRestrictionDTO.getRateRelationsRestriction().getLockedTicketsNumber() != null) {
                throw new OneboxRestException(ApiMgmtErrorCode.TICKET_NUMBER_EXCLUSION_INPUT);
            }
            if (BooleanUtils.isNotTrue(rateRestrictionDTO.getRateRelationsRestriction().getUseAllZonePrices())
                    && CollectionUtils.isEmpty(rateRestrictionDTO.getRateRelationsRestriction().getRestrictedPriceTypeIds())) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_RATE_RESTRICTIONS);
            }
        }

        if ( BooleanUtils.isTrue(rateRestrictionDTO.getMaxItemRestrictionEnabled())
                && (rateRestrictionDTO.getMaxItemRestriction() == null || rateRestrictionDTO.getMaxItemRestriction() <= 0)) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.INVALID_RATE_RESTRICTIONS)
                    .setMessage("Max item restriction must be greater than 0 when enabled.")
                    .build();
        }
    }


    public static void validateSeasonTicketRestrictions( RateRestrictionDTO rateRestrictionDTO) {
        if (rateRestrictionDTO == null) {
            return;
        }

        if (rateRestrictionDTO.getRateRelationsRestriction() != null) {
            if (rateRestrictionDTO.getRateRelationsRestriction().getRequiredTicketsNumber() != null
                    && rateRestrictionDTO.getRateRelationsRestriction().getLockedTicketsNumber() != null) {
                throw new OneboxRestException(ApiMgmtErrorCode.TICKET_NUMBER_EXCLUSION_INPUT);
            }
            if (BooleanUtils.isNotTrue(rateRestrictionDTO.getRateRelationsRestriction().getUseAllZonePrices())
                    && CollectionUtils.isEmpty(rateRestrictionDTO.getRateRelationsRestriction().getRestrictedPriceTypeIds())) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_RATE_RESTRICTIONS);
            }
        }
    }


}
