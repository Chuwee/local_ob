package es.onebox.common.datasources.ms.entity.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.ms.entity.MsEntityDatasource;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.dto.UserSearchFilter;
import es.onebox.common.datasources.ms.entity.dto.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UsersRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public UsersRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    @Cached(key = "userByUsername", expires = 5 * 60)
    public User getByUsernameCached(@CachedArg String username, @CachedArg Long operatorId) {
        return msEntityDatasource.getUser(username, null, operatorId);
    }

    public User getByUsername(String username, Long operatorId) {
        return msEntityDatasource.getUser(username, null, operatorId);
    }

    @Cached(key = "userByApiKey", expires = 5 * 60)
    public User getByApiKey(@CachedArg String apiKey, @CachedArg Long operatorId) {
        return msEntityDatasource.getUser(null, apiKey, operatorId);
    }

    @Cached(key = "userById", expires = 5 * 60)
    public User getByIdCached(@CachedArg Long userId) {
        return msEntityDatasource.getUser(userId);
    }

    public User getById(Long userId) {
        return msEntityDatasource.getUser(userId);
    }

    @Cached(key = "usersByFilter", expires = 5 * 60)
    public Users getFilteredUsers(@CachedArg UserSearchFilter userFilter) {
        return msEntityDatasource.getUsers(userFilter);
    }

    public void updateUser(Long userId, User userUpdate) {
        msEntityDatasource.updateUser(userId, userUpdate);
    }
}
