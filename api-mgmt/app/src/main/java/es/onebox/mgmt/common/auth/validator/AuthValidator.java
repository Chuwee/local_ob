package es.onebox.mgmt.common.auth.validator;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.auth.dto.AuthConfigDTO;
import es.onebox.mgmt.common.auth.dto.AuthenticatorDTO;
import es.onebox.mgmt.common.auth.enums.AuthenticatorTypeDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

public class AuthValidator {

    private AuthValidator() {
    }

    public static void validateAuthConfig(AuthConfigDTO in) {

        if (CollectionUtils.isNotEmpty(in.getAuthenticators())) {
            in.getAuthenticators().forEach(AuthValidator::validateAuthenticator);
        }

        if (in.getSettings() != null && BooleanUtils.isTrue(in.getSettings().getBlockedCustomerTypesEnabled())
                && CollectionUtils.isEmpty(in.getSettings().getBlockedCustomerTypes())) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_AUTH_CONFIG_BLOCKED_CUSTOMER_TYPES);
        }

    }

    private static void validateAuthenticator(AuthenticatorDTO in) {
        if (in.getType() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_AUTH_CONFIG_AUTHENTICATORS);
        }

        if (AuthenticatorTypeDTO.DEFAULT.equals(in.getType()) && in.getId() != null) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_AUTH_CONFIG_AUTHENTICATORS);
        }
    }
}
