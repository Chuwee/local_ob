package es.onebox.atm.access;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.cache.annotation.SkippedCachedArg;
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
import java.util.concurrent.TimeUnit;

@Service
public class ATMAccessService {

    private static final String ATM_WEBHOOK_USER_EMAIL = "atm_webhook_user@oneboxtds.com";

    private static final Logger LOGGER = LoggerFactory.getLogger(ATMAccessService.class);
    private final UsersRepository usersRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public ATMAccessService(UsersRepository usersRepository, TokenRepository tokenRepository) {
        this.usersRepository = usersRepository;
        this.tokenRepository = tokenRepository;
    }

    @Cached(key = "accessToken", expires = 10, timeUnit = TimeUnit.MINUTES)
    public String getAccessToken(@CachedArg Long entityId, @SkippedCachedArg String tag, @SkippedCachedArg String orderCode) {
        UserSearchFilter userFilter = new UserSearchFilter();
        userFilter.setEmail(ATM_WEBHOOK_USER_EMAIL);
        userFilter.setEntityId(entityId);
        userFilter.setRole(Roles.ROLE_ENT_ANS);
        userFilter.setSingleRole(true);
        List<User> users = usersRepository.getFilteredUsers(userFilter).getData();
        if (users == null || users.size() != 1) {
            String detailedMessage = users == null ? "User not found" : users.size() + " users found";
            LOGGER.error("{}[{}] User not found or ambiguous user for entityId: {} detail: {}", tag, orderCode,
                    entityId, detailedMessage);
            throw new OneboxRestException(ApiExternalErrorCode.USER_NOT_FOUND);
        }
        User u = users.get(0);
        try {
            return tokenRepository.getOneboxClientToken(u.getId(), u.getApiKey());
        } catch (Exception e) {
            LOGGER.error("{}[{}] Error obtaining oauth token for user: {}", tag, orderCode, u.getUsername());
            throw new OneboxRestException(ApiExternalErrorCode.FORBIDDEN_USER_FOR_WEBHOOK);
        }
    }
}
