package es.onebox.common.utils;

import es.onebox.common.auth.builder.AuthenticationDataBuilder;
import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.datasources.ms.entity.enums.EntityType;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.security.Roles;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class AuthenticationUtils {

    private AuthenticationUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static boolean isValidEntityType(List<EntityType> types) {
        return isAnyOf(types, EntityType.OPERATOR, EntityType.CHANNEL_ENTITY, EntityType.EVENT_ENTITY);
    }

    public static boolean isOperatorEntityType(List<EntityType> types) {
        return isAnyOf(types, EntityType.OPERATOR);
    }

    private static boolean isAnyOf(List<EntityType> types, EntityType... typesToValidate) {
        return Stream.of(typesToValidate).anyMatch(types::contains);
    }

    public static AuthenticationData getAuthDataOrNull(){
        BearerTokenAuthentication auth = (BearerTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if(auth == null){
            return null;
        }

        String username = auth.getPrincipal().toString();
        return new AuthenticationDataBuilder()
                .withUsername(username)
                .withAuthorities(auth.getAuthorities())
                .withClientId(AuthContextUtils.getClientId())
                .build();
    }

    public static boolean hasAnyRole(Roles... requiredRoles) {
        AuthenticationData auth = getAuthDataOrNull();
        if (auth == null) {
            return false;
        }
        final List<String> requiredRolesList = Arrays.stream(requiredRoles).map(Roles::getRol).toList();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(requiredRolesList::contains);
    }

    public static boolean hasEntityType(EntityTypes entityType) {
        AuthenticationData auth = getAuthDataOrNull();
        if (auth == null) {
            return false;
        }
        boolean ret = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> Roles.valueOf(a).getTipoEntidad().contains(entityType));
        return ret;
    }

    public static Long getOperatorId() {
        AuthenticationData auth = getAuthDataOrNull();
        if (auth == null) {
            return null;
        }
        return auth.getOperatorId();
    }

    public static Long getEntityId() {
        AuthenticationData auth = getAuthDataOrNull();
        if (auth == null) {
            return null;
        }
        return auth.getEntityId();
    }

    public static String getCurrentJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof BearerTokenAuthentication bearerAuth) {
            return bearerAuth.getToken().getTokenValue();
        }
        return null;
    }
}
