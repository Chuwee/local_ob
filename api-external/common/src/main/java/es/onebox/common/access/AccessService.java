package es.onebox.common.access;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.dto.UserSearchFilter;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessService.class);
    private final UsersRepository usersRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public AccessService(UsersRepository usersRepository, TokenRepository tokenRepository) {
        this.usersRepository = usersRepository;
        this.tokenRepository = tokenRepository;
    }

    @Cached(key = "accessToken", expires = 3 * 60)
    public String getAccessToken(@CachedArg Long entityId) {
        UserSearchFilter userFilter = new UserSearchFilter();
        userFilter.setEntityId(entityId);
        userFilter.setRole(Roles.ROLE_ENT_MGR);
        List<User> users = usersRepository.getFilteredUsers(userFilter).getData();
        if(users == null || users.isEmpty()) {
            LOGGER.error("There is no users for entity {}", entityId);
            throw new OneboxRestException(ApiExternalErrorCode.USER_NOT_FOUND);
        }
        String token = null;
        for (User u : users) {
            try {
                token = tokenRepository.getOneboxClientToken(u.getId(), u.getApiKey());
                break;
            } catch (Exception e) {
                // continue trying
            }
        }
        return token;
    }

    @Cached(key = "accessOperatorToken", expires = 3 * 60)
    public String getOperatorAccessToken(@CachedArg Long operatorId) {
        UserSearchFilter userFilter = new UserSearchFilter();
        userFilter.setEntityId(operatorId);
        userFilter.setRole(Roles.ROLE_OPR_MGR);
        List<User> users = usersRepository.getFilteredUsers(userFilter).getData();
        if(users == null || users.isEmpty()) {
            LOGGER.error("There is no users for operator {}", operatorId);
            throw new OneboxRestException(ApiExternalErrorCode.USER_NOT_FOUND);
        }
        String token = null;
        for (User u : users) {
            try {
                token = tokenRepository.getOneboxClientToken(u.getId(), u.getApiKey());
                break;
            } catch (Exception e) {
                // continue trying
            }
        }
        return token;
    }
}
