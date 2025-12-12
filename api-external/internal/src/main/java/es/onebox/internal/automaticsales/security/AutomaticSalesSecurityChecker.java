package es.onebox.internal.automaticsales.security;

import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.datasources.ms.entity.dto.RoleDTO;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.oauth2.dto.UserAuthentication;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.security.Role;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.internal.automaticsales.utils.AuthenticationUtils;
import es.onebox.internal.config.InternalApiConfig;
import jakarta.servlet.ServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class AutomaticSalesSecurityChecker implements SecurityChecker {

    private static final String AUTOMATIC_SALES_PERMISSION = "AUTOMATIC_SALES";

    private final AuthenticationService authenticationService;
    private final UsersRepository usersRepository;

    public AutomaticSalesSecurityChecker(AuthenticationService authenticationService, UsersRepository usersRepository) {
        this.authenticationService = authenticationService;
        this.usersRepository = usersRepository;
    }


    @Override
    public boolean validateEntity() {
        return true;
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, InternalApiConfig.AutomaticSales.SUBCONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return InternalApiConfig.AutomaticSales.CLIENT_ID.contains(clientId);
    }

    @Override
    public void authentication() {
        authenticationService.validateAuthentication();
        UserAuthentication userAuthentication = AuthenticationUtils.getUserAuthentication();
        User user = usersRepository.getByUsername(userAuthentication.getUser(), AuthenticationService.getAuthDataOperatorId());
        if(!hasAutomaticSalesPermission(user)) {
            throw OneboxRestException.builder(ApiExternalErrorCode.AUTH_USER_PERMISSION_INVALID).build();
        }
    }

    private boolean hasAutomaticSalesPermission(User user) {
        for (RoleDTO role : user.getRoles()) {
            if (Role.OPERATOR_MANAGER.equals(role.getCode()) && !CollectionUtils.isEmpty(role.getPermissions())
                    && (role.getPermissions().stream().anyMatch(permission -> AUTOMATIC_SALES_PERMISSION.equals(permission.getCode())))) {
                return true;
            }
        }
        return false;
    }
}
