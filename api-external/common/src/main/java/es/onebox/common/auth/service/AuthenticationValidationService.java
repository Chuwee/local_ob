package es.onebox.common.auth.service;


import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.auth.dto.UserData;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.enums.EntityState;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.entities.dto.IdValueDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.AuthErrorCode;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationValidationService {

    private final UsersRepository usersRepository;
    private final EntitiesRepository entitiesRepository;


    @Autowired
    public AuthenticationValidationService(UsersRepository usersRepository, EntitiesRepository entitiesRepository) {
        this.usersRepository = usersRepository;
        this.entitiesRepository = entitiesRepository;
    }

    public UserData validate(AuthenticationData authData) {
        return validateClient(authData);
    }

    private UserData validateClient(AuthenticationData authData) {
        EntityDTO entity = validateEntity(authData.getEntityId());
        EntityDTO operator = validateOperator(entity);

        return validatePlatformUser(authData.getUsername(), authData.getPassword(), entity, operator);
    }

    private EntityDTO validateEntity(Long entityId) {
        EntityDTO entity = validateEntity(entityId, AuthErrorCode.AUTH_ENTITY_NOT_FOUND, AuthErrorCode.AUTH_ENTITY_INACTIVE);
        validate(AuthenticationUtils.isValidEntityType(entity.getTypes()), ApiExternalErrorCode.ACCESS_DENIED);
        return entity;
    }

    private EntityDTO validateOperator(EntityDTO entity) {
        Long operatorId = entity.getOperator().getId();
        if (operatorId.equals(entity.getId())) {
            return entity;
        }
        return validateEntity(operatorId, AuthErrorCode.AUTH_OPERATOR_NOT_FOUND, AuthErrorCode.AUTH_OPERATOR_INACTIVE);
    }

    private EntityDTO validateEntity(Long entityId, AuthErrorCode errorNotfound, AuthErrorCode errorInactive) {
        EntityDTO entity = entitiesRepository.getByIdCached(entityId);
        validate(entity != null, errorNotfound);
        validate(EntityState.ACTIVE == entity.getState(), errorInactive);
        return entity;
    }



    private UserData validatePlatformUser(String username, String password, EntityDTO entity, EntityDTO operator) {
        User user = usersRepository.getByUsername(username, operator.getId());
        validate(user != null, AuthErrorCode.AUTH_USER_NOT_FOUND);
        validate(password == null || password.equals(user.getPassword()), AuthErrorCode.AUTH_PASSWORD_INCORRECT);
        return validatePlatformUser(user, entity, operator);
    }

    private UserData validatePlatformUser(User user, EntityDTO entity, EntityDTO operator) {
        validate(user.getEntityId().equals(entity.getId()), AuthErrorCode.AUTH_USER_ENTITY_INCORRECT);
        return buildUserData(user.getUsername(), user.getId(), entity, operator);
    }

    private void validate(boolean validation, ErrorCode errorCode) {
        if (!validation) {
            throw OneboxRestException.builder(errorCode).build();
        }
    }

    private UserData buildUserData(String username, Long userId, EntityDTO entity, EntityDTO operator) {
        UserData userData = new UserData();
        userData.setId(userId);
        userData.setUsername(username);
        userData.setEntityId(entity.getId());
        userData.setEntityTypes(entity.getTypes());
        userData.setOperatorId(operator.getId());
        userData.setOperatorTimeZone(Optional.ofNullable(operator.getTimezone()).map(IdValueDTO::getValue).orElse(null));
        return userData;
    }

}
