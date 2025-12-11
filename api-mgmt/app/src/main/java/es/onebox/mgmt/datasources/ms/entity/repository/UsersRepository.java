package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.cache.annotation.SkippedCachedArg;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.ForgotPwdResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.MFARequest;
import es.onebox.mgmt.datasources.ms.entity.dto.MFAResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.Notification;
import es.onebox.mgmt.datasources.ms.entity.dto.RecoverForgotPasswordRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.Role;
import es.onebox.mgmt.datasources.ms.entity.dto.Roles;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.dto.UserAuthUrls;
import es.onebox.mgmt.datasources.ms.entity.dto.UserFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.Users;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.ResourceServer;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.UserRealmConfigCreateDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.UserRealmConfigDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.datasources.ms.entity.dto.ForgotPasswordPropertiesResponse;
import es.onebox.mgmt.users.dto.UpdateVisibilityDTO;
import es.onebox.mgmt.users.dto.UserSecretDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsersRepository {

    private static final String CACHE_AUTHUSER_KEY = "entities.authuser";
    private static final String CACHE_REPORTS_URLS_TOKEN_KEY = "users.reports.urls.token";
    private static final int CACHE_REPORTS_URLS_TOKEN_TTL = 30;
    private static final String CACHE_REPORTS_HAS_SUBSCRIPTIONS_TOKEN_KEY = "users.reports.hassubsciptions.token";
    private static final int CACHE_REPORTS_HAS_SUBSCRIPTIONS_TOKEN_TTL = 30;
    private static final String CACHE_REPORTS_CAN_IMPERSONATE_TOKEN_KEY = "users.reports.canimpersonate.token";
    private static final int CACHE_REPORTS_CAN_IMPERSONATE_TOKEN_TTL = 30;
    private static final String CACHE_REPORTS_IS_SUPERSET_USER_TOKEN_KEY = "users.reports.issupersetuser.token";
    private static final int CACHE_REPORTS_IS_SUPERSET_USER_TOKEN_TTL = 60;

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public UsersRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public User getById(Long userId) {
        return msEntityDatasource.getUser(userId);
    }

    @Cached(key = CACHE_AUTHUSER_KEY)
    public User getAuthUserByUserName(@CachedArg String username, @CachedArg Long entityId) {
        return msEntityDatasource.getAuthUserByUserName(username, entityId);
    }

    @Cached(key = CACHE_AUTHUSER_KEY)
    public User getAuthUserByApiKey(@CachedArg String apiKey, @CachedArg Long entityId) {
        return msEntityDatasource.getAuthUserByApiKey(apiKey, entityId);
    }

    public User getUser(String username, Long operatorId, String apiKey) {
        final Users users = msEntityDatasource.getUser(username, operatorId, apiKey);
        if (users.getData().size() == 1) {
            return users.getData().get(0);
        }
        throw OneboxRestException.builder(ApiMgmtErrorCode.NOT_FOUND).
                setMessage("User not found for username " + username).build();
    }

    public Users getUsers(UserFilter request) {
        return msEntityDatasource.getUsers(request);
    }

    public UserSecretDTO create(User user) {
        return msEntityDatasource.createUser(user);
    }

    public void update(User user) {
        msEntityDatasource.updateUser(user);
    }

    public void delete(Long userId) {
        msEntityDatasource.deleteUser(userId);
    }

    public List<Role> getUserRoles(Long userId) {
        return msEntityDatasource.getUserRoles(userId);
    }

    public List<ResourceServer> getAvailableResourceServers(Long userId) {
        return msEntityDatasource.getAllAvailableResourceServers(userId);
    }

    public UserRealmConfigDTO getUserRealmConfig(Long userId) {
        try {
            return msEntityDatasource.getUserRealmConfig(userId);
        } catch (OneboxRestException e) {
            if (ApiMgmtErrorCode.NOT_FOUND.name().equals(e.getErrorCode())) {
                return null;
            } else {
                throw e;
            }
        }
    }

    public void upsertUserRealConfig(Long userId, UserRealmConfigCreateDTO upsert) {
        msEntityDatasource.upsertUserRealmConfig(userId, upsert);
    }

    public void setRole(Long userId, Role role) {
        msEntityDatasource.setRole(userId, role);
    }

    public void setRoles(Long userId, Roles roles) {
        msEntityDatasource.setRoles(userId, roles);
    }

    public void unsetRole(Long userId, String roleCode) {
        msEntityDatasource.unsetRole(userId, roleCode);
    }

    public void addPermission(Long userId, String roleCode, String permissionCode) {
        msEntityDatasource.addPermission(userId, roleCode, permissionCode);
    }

    public void deletePermission(Long userId, String roleCode, String permissionCode) {
        msEntityDatasource.deletePermission(userId, roleCode, permissionCode);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return msEntityDatasource.getUserNotifications(userId);
    }

    public void setUserNotifications(Long userId, List<Notification> notifications) {
        msEntityDatasource.setUserNotifications(userId, notifications);
    }

    public ForgotPwdResponse forgotPassword(String email) {
        return msEntityDatasource.forgotPassword(email);
    }

    public ForgotPasswordPropertiesResponse validateToken(String token) {
        return msEntityDatasource.validateToken(token);
    }

    public void recoverForgotPassword(RecoverForgotPasswordRequest request) {
        msEntityDatasource.recoverForgotPassword(request);
    }

    @Cached(key = CACHE_REPORTS_URLS_TOKEN_KEY, expires = CACHE_REPORTS_URLS_TOKEN_TTL)
    public UserAuthUrls getUserAuthUrls(@CachedArg Long userId, @SkippedCachedArg boolean oldCpanel, @CachedArg Long impersonatedUserId) {
        return msEntityDatasource.getUserAuthUrls(userId, oldCpanel, impersonatedUserId);
    }

    @Cached(key = CACHE_REPORTS_HAS_SUBSCRIPTIONS_TOKEN_KEY, expires = CACHE_REPORTS_HAS_SUBSCRIPTIONS_TOKEN_TTL)
    public Boolean userHasSubscriptions(@CachedArg Long userId) {
        return msEntityDatasource.userHasSubscriptions(userId);
    }

    @Cached(key = CACHE_REPORTS_IS_SUPERSET_USER_TOKEN_KEY, expires = CACHE_REPORTS_IS_SUPERSET_USER_TOKEN_TTL)
    public Boolean isSupersetUser(@CachedArg Long userId) {
        return msEntityDatasource.isSupersetUser(userId);
    }

    @Cached(key = CACHE_REPORTS_CAN_IMPERSONATE_TOKEN_KEY, expires = CACHE_REPORTS_CAN_IMPERSONATE_TOKEN_TTL)
    public Boolean userCanImpersonate(@CachedArg Long userId) {
        return msEntityDatasource.userCanImpersonate(userId);
    }

    public UserSecretDTO refreshApiKey(Long userId) {
        return msEntityDatasource.refreshApiKey(userId);
    }

    public MFAResponse sendMFAActivationEmail(Long userId, MFARequest request) {
        return msEntityDatasource.sendMFAActivationEmail(userId, request);
    }

    public MFAResponse validateAndActivateMFA(Long userId, MFARequest request) {
        return msEntityDatasource.validateAndActivateMFA(userId, request);
    }

    public void updateUserVisibility(Long userId, UpdateVisibilityDTO request) {
        msEntityDatasource.updateUserVisibility(userId, request);
    }
}
