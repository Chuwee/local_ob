package es.onebox.common.auth.service;

import es.onebox.common.auth.builder.AuthenticationDataBuilder;
import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.auth.dto.UserData;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final ThreadLocal<AuthenticationData> AUTH_DATA = new ThreadLocal<>();
    private static final ThreadLocal<UserData> AUTH_USER = new ThreadLocal<>();

    private final AuthenticationValidationService authenticationValidationService;

    @Autowired
    public AuthenticationService(AuthenticationValidationService authenticationValidationService) {
        this.authenticationValidationService = authenticationValidationService;
    }

    public void validateAuthentication() {
        AuthenticationData authenticationData = AUTH_DATA.get();
        if (authenticationData != null) {
            UserData user = authenticationValidationService.validate(authenticationData);
            AUTH_USER.set(user);
        }
    }

    public boolean initAuthContext() {

        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            return false;
        }
        BearerTokenAuthentication authentication = (BearerTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }
        String username = authentication.getName();
        AuthenticationData authenticationData = new AuthenticationDataBuilder()
                .withUsername(username)
                .withAuthorities(authentication.getAuthorities())
                .withClientId(AuthContextUtils.getClientId())
                .build();
        AUTH_DATA.set(authenticationData);
        return true;
    }

    public void finishAuthContext() {
        AUTH_DATA.remove();
        AUTH_USER.remove();
    }

    public static Long getAuthDataOperatorId() {
        return getAuthData().getOperatorId();
    }

    public static Long getAuthDataEntityId() {
        return getAuthData().getEntityId();
    }

    public static String getUsername() {
        return getUser().getUsername();
    }

    public static Long getEntityId() {
        return getUser().getEntityId();
    }


    public static boolean isOperatorEntityType() {
        return AuthenticationUtils.isOperatorEntityType(getUser().getEntityTypes());
    }

    public static String getClientId() {
        return getAuthData().getClientId();
    }

    public static Long getOperatorId() {
        return getUser().getOperatorId();
    }

    private static UserData getUser() {
        return AUTH_USER.get();
    }

    private static AuthenticationData getAuthData() {
        return AUTH_DATA.get();
    }
}
