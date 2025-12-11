package es.onebox.mgmt.security;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.security.Roles;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.oauth2.resource.utils.TokenParam;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;

import static es.onebox.core.security.Roles.ROLE_ENT_ADMIN;
import static es.onebox.core.security.Roles.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.ROLE_SYS_MGR;

public class SecurityUtils {

    public static final String USER_PASSWORD = "userPassword";
    public static final String AUTH_ENTITY_ID = "entityId";
    public static final String API_KEY = "apiKey";
    public static final String AUTH_USER_ID = "userId";
    public static final String AUTH_OPERATOR_ID = "operatorId";

    private static final Roles[] SUPER_OPERATOR = getRoleByTypeEntity(EntityTypes.SUPER_OPERATOR).toArray(new Roles[0]);

    private SecurityUtils() {
    }

    public static boolean authenticatedUser() {
        return SecurityContextHolder.getContext().getAuthentication() instanceof BearerTokenAuthentication;
    }

    public static BearerTokenAuthentication getUserAuth() {
        return (BearerTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean hasAnyRole(Roles... requiredRoles) {
        final List<String> requiredRolesList = Arrays.stream(requiredRoles).map(Roles::getRol).toList();
        return getUserAuth().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(requiredRolesList::contains);
    }

    public static boolean hasEntityType(EntityTypes entityType) {
        return getUserAuth().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> Roles.valueOf(a).getTipoEntidad().contains(entityType));
    }

    public static boolean hasAnyEntityTypes(EntityTypes... entityTypes) {
        return Arrays.stream(entityTypes).map(SecurityUtils::hasEntityType).anyMatch(BooleanUtils::isTrue);
    }

    public static boolean isOperatorEntity() {
        return hasEntityType(EntityTypes.OPERATOR);
    }

    public static boolean accessibleResource(Long entityId, Long operatorId, Roles... requiredRoles) {
        return hasAnyRole(ROLE_SYS_MGR, ROLE_SYS_ANS) || ((entityId.equals(getUserEntityId()) && (requiredRoles.length == 0 || hasAnyRole(requiredRoles)))
                || (operatorId.equals(getUserOperatorId()) && hasAnyRole(ROLE_OPR_MGR, ROLE_OPR_ANS))
                || (hasAnyRole(SUPER_OPERATOR)));
    }

    public static boolean notAccessibleResource(Long entityId, Long operatorId, List<Long> managedEntities, Roles... requiredRoles) {
        return !accessibleResource(entityId, operatorId, requiredRoles) && (!hasAnyRole(ROLE_ENT_ADMIN) || !managedEntities.contains(entityId));
    }

    public static String getUsername() {
        return getUserAuth().getName();
    }

    public static long getUserEntityId() {
        var param = (Number) getAuthDetails().get(AUTH_ENTITY_ID);
        return param.longValue();
    }

    public static long getUserId() {
        var param = (Number) getAuthDetails().get(AUTH_USER_ID);
        return param.longValue();
    }

    public static long getUserOperatorId() {
        var param = (Number) getAuthDetails().get(AUTH_OPERATOR_ID);
        return param.longValue();
    }

    public static String getApiKey() {
        return (String) getAuthDetails().get(API_KEY);
    }

    public static String getPassword() {
        return (String) getAuthDetails().get(USER_PASSWORD);
    }


    public static long getUserEntityId(Long requestEntityId, LongConsumer checkEntityAccessible, Boolean restrictOperator) {
        if (hasAnyRole(ROLE_OPR_MGR, ROLE_OPR_ANS)) {
            if (requestEntityId != null) {
                checkEntityAccessible.accept(requestEntityId);
                return requestEntityId;
            }
            if (BooleanUtils.isTrue(restrictOperator)) {
                throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER).setMessage("entity_id is mandatory").build();
            }
            return getUserEntityId();
        }
        long userEntity = getUserEntityId();
        if (requestEntityId != null && requestEntityId != userEntity) {
            throw new AccessDeniedException("Can't access resources from other entities or operators");
        }
        return userEntity;
    }

    public static long getUserEntityId(Long requestEntityId, LongConsumer checkEntityAccessible) {
        return getUserEntityId(requestEntityId, checkEntityAccessible, Boolean.TRUE);
    }

    public static boolean hasAnyEntityType(List<EntityTypes> currentEntityTypes, EntityTypes... requiredEntityType) {
        final List<Integer> requiredRolesList = Arrays.stream(requiredEntityType)
                .map(EntityTypes::getType).toList();
        return currentEntityTypes.stream()
                .map(EntityTypes::getType).toList()
                .stream().anyMatch(requiredRolesList::contains);
    }


    public static List<Roles> getRoleByTypeEntity(EntityTypes entityType) {
        return Arrays.stream(Roles.values())
                .filter(role -> role.getTipoEntidad().contains(entityType))
                .collect(Collectors.toList());
    }

    public static Map<String, Object> getAuthDetails() {
        return (Map<String, Object>) getUserAuth().getTokenAttributes().get(TokenParam.AUTH_INFO.value());
    }

}
