package es.onebox.fever.service;

import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.dto.Operator;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.dto.UserSearchFilter;
import es.onebox.common.datasources.ms.entity.dto.Users;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.fever.dto.FvUserAuth;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthUserService {

    private final EntitiesRepository entitiesRepository;
    private final UsersRepository usersRepository;

    public AuthUserService(EntitiesRepository entitiesRepository, UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
        this.entitiesRepository = entitiesRepository;
    }

    public FvUserAuth getUserInfo(Long entityId) {
        EntityDTO entity = entitiesRepository.getByIdCached(entityId);
        AuthenticationData authData = AuthenticationUtils.getAuthDataOrNull();
        validateEntity(entity, authData);

        UserSearchFilter userSearchFilter = new UserSearchFilter();
        userSearchFilter.setApiKey(authData.getApiKey());
        userSearchFilter.setEntityId(authData.getEntityId());
        userSearchFilter.setRole(Roles.ROLE_FV_REPORTING);

        Users users = this.usersRepository.getFilteredUsers(userSearchFilter);
        User user = users == null || CollectionUtils.isEmpty(users.getData()) ? null : users.getData().get(0);
        validateUser(user, users);

        FvUserAuth userAuth = new FvUserAuth();
        userAuth.setId(user.getId());
        userAuth.setFvId(Integer.valueOf(user.getExternalReference()));
        userAuth.setEmail(user.getEmail());
        userAuth.setObEntityId(entity.getId());
        userAuth.setFvPartnerId(Integer.valueOf(entity.getExternalReference()));
        return userAuth;
    }

    private void validateEntity(EntityDTO entity, AuthenticationData authData) {
        if (entity == null || authData == null || (!entity.getId().equals(authData.getEntityId()) &&
                !entity.getOperator().getId().equals(authData.getEntityId()))) {
            throw new OneboxRestException(ApiExternalErrorCode.ENTITY_NOT_FOUND);
        }
        if (StringUtils.isEmpty(entity.getExternalReference()) || !Boolean.TRUE.equals(entity.getAllowFeverZone())) {
            throw new OneboxRestException(ApiExternalErrorCode.FV_ZONE_NOT_AVAILABLE);
        }
        Operator operator = this.entitiesRepository.getCachedOperator(entity.getOperator().getId());
        if (operator == null || !Boolean.TRUE.equals(operator.getAllowFeverZone())) {
            throw new OneboxRestException(ApiExternalErrorCode.FV_ZONE_NOT_AVAILABLE);
        }
    }

    private void validateUser(User user, Users users) {
        if (user == null || users.getData().size() > 1) {
            throw new OneboxRestException(ApiExternalErrorCode.USER_NOT_FOUND);
        }
        if (StringUtils.isEmpty(user.getExternalReference())) {
            throw new OneboxRestException(ApiExternalErrorCode.FV_ZONE_NOT_AVAILABLE);
        }
    }
}
