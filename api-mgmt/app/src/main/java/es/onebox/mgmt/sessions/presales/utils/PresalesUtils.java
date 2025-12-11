package es.onebox.mgmt.sessions.presales.utils;

import es.onebox.mgmt.datasources.ms.event.dto.session.PreSaleConfigDTO;
import es.onebox.mgmt.sessions.enums.PresaleStatus;
import es.onebox.mgmt.sessions.enums.PresaleValidationRangeType;
import org.apache.commons.lang3.BooleanUtils;

import java.time.ZonedDateTime;

public class PresalesUtils {

    public static PresaleStatus getPresaleStatus(PreSaleConfigDTO dto) {
        if (BooleanUtils.isNotTrue(dto.getActive())) {
            return PresaleStatus.INACTIVE;
        }
        if (PresaleValidationRangeType.ALL.equals(dto.getValidationRangeType())) {
            return PresaleStatus.ACTIVE;
        }
        ZonedDateTime now = ZonedDateTime.now();
        if (now.isBefore(dto.getEndDate()) && now.isAfter(dto.getStartDate())) {
            return PresaleStatus.ACTIVE;
        }
        return PresaleStatus.PLANNED;
    }
}
