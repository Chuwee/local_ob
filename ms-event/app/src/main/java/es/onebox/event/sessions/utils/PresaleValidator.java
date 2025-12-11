package es.onebox.event.sessions.utils;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.sessions.dto.CreateSessionPreSaleConfigDTO;
import es.onebox.event.sessions.dto.UpdateSessionPreSaleConfigDTO;
import es.onebox.event.sessions.enums.PresaleValidationRangeType;
import es.onebox.event.sessions.enums.PresaleValidatorType;
import es.onebox.jooq.cpanel.tables.records.CpanelPreventaRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class PresaleValidator {

    public PresaleValidator(){
    }

    public static void validateCreatePresaleRequest(CreateSessionPreSaleConfigDTO request) {
        if (ObjectUtils.anyNull(request, request.getName(), request.getValidatorType())) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_SESSION_PRESALE_CREATE_REQUEST);
        }
        if ((PresaleValidatorType.COLLECTIVE.equals(request.getValidatorType()) && isNull(request.getValidatorId()))
                || (PresaleValidatorType.CUSTOMERS.equals(request.getValidatorType()) && nonNull(request.getValidatorId()))) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_SESSION_PRESALE_CREATE_REQUEST);
        }
    }

    public static void validateUpdatePresaleRequest(CpanelPreventaRecord preventaRecord, UpdateSessionPreSaleConfigDTO request) {
        if (PresaleValidationRangeType.DATE_RANGE.equals(request.getValidationRangeType())) {
            if (isNull(request.getStartDate()) || isNull(request.getEndDate())) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_SESSION_PRESALE_DATES_REQUIRED);
            }
            if (request.getEndDate().isBefore(request.getStartDate())) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_SESSION_PRESALE_DATES_END_BEFORE_START);
            }
        }

        if (!PresaleValidatorType.CUSTOMERS.getId().equals(preventaRecord.getTipovalidador())) {
            if (CollectionUtils.isNotEmpty(request.getActiveCustomerTypes())) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_SESSION_PRESALE_UPDATE_REQUEST);
            }
            if ((BooleanUtils.isTrue(request.getMemberTicketsLimitEnabled())|| request.getMemberTicketsLimit() != null)) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_SESSION_PRESALE_UPDATE_REQUEST);
            }
        }
    }
}
