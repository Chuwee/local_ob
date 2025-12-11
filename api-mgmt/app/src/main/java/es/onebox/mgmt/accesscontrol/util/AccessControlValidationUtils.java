package es.onebox.mgmt.accesscontrol.util;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.NameDTO;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.exception.ApiMgmtAccessControlErrorCode;
import org.apache.commons.lang3.StringUtils;

public class AccessControlValidationUtils {

    private AccessControlValidationUtils() {
    }

    public static AccessControlSystem validateAccessControlSystem(String system) {
        if (StringUtils.isEmpty(system)) {
            throw new OneboxRestException(ApiMgmtAccessControlErrorCode.ACCESS_CONTROL_SYSTEM_MANDATORY);
        }
        try {
            return AccessControlSystem.getFromApiName(system);
        } catch (IllegalArgumentException e) {
            throw new OneboxRestException(ApiMgmtAccessControlErrorCode.ACCESS_CONTROL_SYSTEM_NOT_FOUND);
        }
    }

    public static AccessControlSystem validateAccessControlSystem(NameDTO system) {
        if (system == null) {
            throw new OneboxRestException(ApiMgmtAccessControlErrorCode.ACCESS_CONTROL_SYSTEM_MANDATORY);
        }
        return validateAccessControlSystem(system.getName());
    }
}
