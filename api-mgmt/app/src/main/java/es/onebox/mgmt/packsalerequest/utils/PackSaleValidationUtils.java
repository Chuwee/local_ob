package es.onebox.mgmt.packsalerequest.utils;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackSalesRequestBase;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.packsalerequest.enums.PackSaleRequestStatus;

public class PackSaleValidationUtils {

    private PackSaleValidationUtils() {throw new UnsupportedOperationException("Cannot instantiate utility class.");}

    public static void checkValidStatusTransition(PackSalesRequestBase detail, PackSaleRequestStatus newStatus) {
        if (PackSaleRequestStatus.PENDING.equals(newStatus) || newStatus.getId().equals(detail.getState().getId())) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_SALE_REQUEST_STATUS_TRANSITION);
        }
    }

    public static boolean isValidProductStatusChange(PackSaleRequestStatus currentStatus, PackSaleRequestStatus updateStatus) {
        if (PackSaleRequestStatus.PENDING.equals(currentStatus)) {
            return PackSaleRequestStatus.ACCEPTED.equals(updateStatus) || PackSaleRequestStatus.REJECTED.equals(updateStatus);
        }

        if (PackSaleRequestStatus.ACCEPTED.equals(currentStatus)) {
            return PackSaleRequestStatus.REJECTED.equals(updateStatus);
        }

        if (PackSaleRequestStatus.REJECTED.equals(currentStatus)) {
            return PackSaleRequestStatus.ACCEPTED.equals(updateStatus);
        }

        return false;
    }
}
