package es.onebox.mgmt.users.service;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.datasource.http.exception.HttpErrorException;
import es.onebox.datasource.http.status.HttpStatus;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.PasswordUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.ForgotPwdResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.MFARequest;
import es.onebox.mgmt.datasources.ms.entity.dto.MFAResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.RecoverForgotPasswordRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.Role;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.dto.UserAuthUrls;
import es.onebox.mgmt.datasources.ms.entity.dto.Users;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.UsersRepository;
import es.onebox.mgmt.entities.converter.EntityConverter;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.entities.dto.EntitySettingsDTO;
import es.onebox.mgmt.entities.dto.SettingsCustomizationDTO;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.operators.converter.OperatorsConverter;
import es.onebox.mgmt.realms.converter.RoleConverter;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.users.converter.UserConverter;
import es.onebox.mgmt.users.dto.BaseUserDTO;
import es.onebox.mgmt.users.dto.CreateUserRequestDTO;
import es.onebox.mgmt.users.dto.ForgotPasswordPropertiesDTO;
import es.onebox.mgmt.users.dto.ForgotPwdRequestDTO;
import es.onebox.mgmt.users.dto.ForgotPwdResponseDTO;
import es.onebox.mgmt.users.dto.MFA;
import es.onebox.mgmt.users.dto.MFARequestDTO;
import es.onebox.mgmt.users.dto.MFAResponseDTO;
import es.onebox.mgmt.users.dto.NotificationDTO;
import es.onebox.mgmt.users.dto.ProducerDTO;
import es.onebox.mgmt.users.dto.RecoverForgotPasswordRequestDTO;
import es.onebox.mgmt.users.dto.SearchUsersResponse;
import es.onebox.mgmt.users.dto.UpdateAuthUserRequestDTO;
import es.onebox.mgmt.users.dto.UpdateUserRequestDTO;
import es.onebox.mgmt.users.dto.UpdateVisibilityDTO;
import es.onebox.mgmt.users.dto.UserAuthUrlsDTO;
import es.onebox.mgmt.users.dto.UserDTO;
import es.onebox.mgmt.users.dto.UserLocationDTO;
import es.onebox.mgmt.users.dto.UserReportsDTO;
import es.onebox.mgmt.users.dto.UserResponseDTO;
import es.onebox.mgmt.users.dto.UserSearchFilter;
import es.onebox.mgmt.users.dto.UserSecretDTO;
import es.onebox.mgmt.users.dto.UserSelfDTO;
import es.onebox.mgmt.users.enums.MFAState;
import es.onebox.mgmt.users.enums.UserStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static es.onebox.core.security.Roles.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.ROLE_SYS_MGR;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.FORBIDDEN_RESOURCE;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.NOT_FOUND;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.USER_NOT_FOUND;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final EntitiesRepository entitiesRepository;
    private final MasterdataService masterdataService;
    private final SecurityManager securityManager;
    private final CacheRepository cacheRepository;

    private static final String CACHE_MYSELF_OPERATOR = "entities.myself";
    private static final String INVALID_CREDENTIALS =  "INVALID_CREDENTIALS";
    private static final Integer TTL = 120;

    @Autowired
    public UsersService(UsersRepository usersRepository, EntitiesRepository entitiesRepository,
                        MasterdataService masterdataService, SecurityManager securityManager,
                        CacheRepository cacheRepository) {
        this.usersRepository = usersRepository;
        this.entitiesRepository = entitiesRepository;
        this.masterdataService = masterdataService;
        this.securityManager = securityManager;
        this.cacheRepository = cacheRepository;
    }

    public UserSelfDTO getAuthUser() {
        Long authEntityId = SecurityUtils.getUserEntityId();
        String apiKey = SecurityUtils.getApiKey();
        Entity entity = entitiesRepository.getCachedEntity(authEntityId);
        String authUsername = SecurityUtils.getUsername();
        User authUser = usersRepository.getUser(authUsername, entity.getOperator().getId(), apiKey);
        List<Role> userRoles = usersRepository.getUserRoles(authUser.getId());

        UserSelfDTO userDTO = fillUserSelfDTO(authUser, new UserSelfDTO());
        if (!CommonUtils.isEmpty(userRoles)) {
            userDTO.setRoles(RoleConverter.convertRoles(userRoles));
            if(userRoles.stream().anyMatch(role -> role.getCode().equals(Roles.ROLE_PRD_ANS.getRol()))) {
                fillProducerDTO(userDTO, authUser.getProducerId());
            }
        }
        fillReportsInfo(userDTO, authUser);

        return userDTO;
    }

    public UserResponseDTO getUser(long userId) {
        User user = null;
        try {
            user = usersRepository.getById(userId);
        } catch (HttpErrorException e) {
            if (!e.getHttpStatus().equals(HttpStatus.NOT_FOUND)) {
                throw e;
            }
        }
        if (user == null || user.getStatus().equals(0)) { //Status 0 = DELETED
            throw new OneboxRestException(USER_NOT_FOUND, "No user found with id: " + userId, null);
        } else if (SecurityUtils.notAccessibleResource(user.getEntityId(), user.getOperatorId(),
                entitiesRepository.getCachedEntityAdminEntities(SecurityUtils.getUserEntityId()), ROLE_OPR_MGR, ROLE_ENT_MGR)) {
            throw new OneboxRestException(FORBIDDEN_RESOURCE);
        }
        return fillUserResponseDTO(user, new UserResponseDTO());
    }

    public SearchUsersResponse search(UserSearchFilter filter) {
        if (filter.getEntityId() != null) {
            securityManager.checkEntityAccessibleIncludeEntityAdmin(filter.getEntityId());
        }

        Users users = usersRepository.getUsers(UserConverter.toMsEntity(filter));

        SearchUsersResponse response = new SearchUsersResponse();
        response.setData(users.getData().stream().
                map(u -> fillUserResponseDTO(u, new UserResponseDTO())).toList());
        response.setMetadata(users.getMetadata());

        return response;
    }

    public UserSecretDTO create(CreateUserRequestDTO user, Boolean sendEmail) {
        Entity entity;
        try {
            entity = entitiesRepository.getCachedEntity(user.getEntityId());
        } catch (HttpErrorException e) {
            throw new OneboxRestException(FORBIDDEN_RESOURCE, "entity does not exist", e);
        }

        if (SecurityUtils.notAccessibleResource(user.getEntityId(), entity.getOperator().getId(),
                entitiesRepository.getCachedEntityAdminEntities(SecurityUtils.getUserEntityId()), ROLE_OPR_MGR, ROLE_ENT_MGR)) {
            throw new OneboxRestException(FORBIDDEN_RESOURCE);
        }

        User newUser = UserConverter.toMsEntity(user);
        newUser.setSendEmail(sendEmail);
        newUser.setOperatorId(entity.getOperator().getId());

        fillLanguage(newUser, user, entity);

        return usersRepository.create(newUser);
    }

    public void update(Long userId, UpdateUserRequestDTO user) {
        User userToUpdate = usersRepository.getById(userId);
        if (!SecurityUtils.accessibleResource(userToUpdate.getEntityId(), userToUpdate.getOperatorId(), ROLE_OPR_MGR, ROLE_ENT_MGR)) {
            throw new OneboxRestException(FORBIDDEN_RESOURCE);
        }
        if (user.getEntityId() != null && !userToUpdate.getEntityId().equals(user.getEntityId())) {
            Entity newUserEntity = entitiesRepository.getCachedEntity(user.getEntityId());
            if (!SecurityUtils.accessibleResource(newUserEntity.getId(), newUserEntity.getOperator().getId(), ROLE_OPR_MGR, ROLE_ENT_MGR)) {
                throw new OneboxRestException(FORBIDDEN_RESOURCE);
            }
        }

        //TODO: Delete this condition when integration roles are protected in cpanel
        if (!SecurityUtils.isOperatorEntity() && userToUpdate.getEmail().startsWith("int_")
                && CollectionUtils.isNotEmpty(userToUpdate.getRoles())
                && userToUpdate.getRoles().size() == 1
                && Roles.ROLE_CNL_INT.name().equals(userToUpdate.getRoles().get(0).getCode())
                && UserStatus.ACTIVE.getId().equals(userToUpdate.getStatus())
                && user.getStatus() != null && !user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new OneboxRestException(ApiMgmtErrorCode.USER_NOT_MODIFIABLE);
        }

        User userDTO = UserConverter.toMsEntity(user);
        userDTO.setId(userId);

        fillLanguage(userDTO, user, null);
        fillLocationIds(userDTO, user);

        usersRepository.update(userDTO);
    }

    public void updateAuthUser(UpdateAuthUserRequestDTO updateRequest){
        Long authEntityId = SecurityUtils.getUserEntityId();
        String apiKey = SecurityUtils.getApiKey();
        Entity entity = entitiesRepository.getCachedEntity(authEntityId);
        String authUsername = SecurityUtils.getUsername();
        User authUser = usersRepository.getUser(authUsername, entity.getOperator().getId(), apiKey);
        if (authUser != null && updateRequest != null) {
            User user = UserConverter.toMsEntity(authUser.getId(), updateRequest);
            fillLanguage(user, updateRequest, null);
            fillLocationIds(user, updateRequest);

            usersRepository.update(user);
        }
    }

    public void delete(Long userId) {
        User userToDelete = usersRepository.getById(userId);
        if (SecurityUtils.notAccessibleResource(userToDelete.getEntityId(), userToDelete.getOperatorId(), entitiesRepository.getCachedEntityAdminEntities(SecurityUtils.getUserEntityId()), ROLE_OPR_MGR, ROLE_ENT_MGR)) {
            throw new OneboxRestException(FORBIDDEN_RESOURCE);
        } else if (userToDelete.getUsername().equals(SecurityUtils.getUsername())) {
            throw new OneboxRestException(FORBIDDEN_RESOURCE, "user cant remove to itself", null);
        }
        //TODO: Delete this condition when integration roles are protected in cpanel
        if (!SecurityUtils.isOperatorEntity() && userToDelete.getEmail().startsWith("int_")
                && UserStatus.ACTIVE.getId().equals(userToDelete.getStatus())
                && CollectionUtils.isNotEmpty(userToDelete.getRoles())
                && userToDelete.getRoles().size() == 1
                && Roles.ROLE_CNL_INT.name().equals(userToDelete.getRoles().get(0).getCode())) {
            throw new OneboxRestException(ApiMgmtErrorCode.USER_NOT_MODIFIABLE);
        }
        usersRepository.delete(userId);
    }

    public void setUserPassword(Long userId, String newPwd, String token) {
        if ((!SecurityUtils.authenticatedUser() || !SecurityUtils.hasAnyRole(ROLE_OPR_MGR, ROLE_SYS_MGR))
                && token == null) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER)
                    .setMessage("Token required")
                    .build();
        }
        if (newPwd == null) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER)
                    .setMessage("Password required")
                    .build();
        }
        User userToModify = usersRepository.getById(userId);
        if (userToModify == null || (!SecurityUtils.hasAnyRole(ROLE_SYS_MGR) && userLoggedButOtherOperator(userToModify))) {
            throw OneboxRestException.builder(NOT_FOUND).build();
        }
        setPassword(userId, newPwd, token);
    }

    public void setSelfPassword(String oldPwd, String newPwd) {
        if (!SecurityUtils.authenticatedUser()) {
            throw new OneboxRestException(ApiMgmtErrorCode.UNAUTHORIZED_ACCESS);
        }
        if (oldPwd == null || newPwd == null) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER)
                    .setMessage("New and old passwords must be setted")
                    .build();
        }

        User user = usersRepository.getUser(SecurityUtils.getUsername(), SecurityUtils.getUserOperatorId(),
                SecurityUtils.getApiKey());

        if (user == null) {
            throw OneboxRestException.builder(NOT_FOUND).build();
        } else if (!user.getPassword().equals(PasswordUtils.getHashMD5(oldPwd))) {
            throw OneboxRestException.builder(ApiMgmtEntitiesErrorCode.CURRENT_PASSWORD_IS_NOT_CORRECT).build();
        }

        setPassword(user.getId(), newPwd, null);
    }

    public ForgotPwdResponseDTO forgotPassword(ForgotPwdRequestDTO request) {
        ForgotPwdResponse response = usersRepository.forgotPassword(request.email());
        return new ForgotPwdResponseDTO(response.email());
    }

    public ForgotPasswordPropertiesDTO validateToken(String token) {
        return UserConverter.toDTO(usersRepository.validateToken(token));
    }

    public void recoverForgotPassword(RecoverForgotPasswordRequestDTO request) {
        RecoverForgotPasswordRequest requestToMs = new RecoverForgotPasswordRequest();
        requestToMs.setNewPassword(request.newPassword());
        requestToMs.setToken(request.token());
        usersRepository.recoverForgotPassword(requestToMs);
    }

    public UserAuthUrlsDTO getMstrUrls(Long userId, Long impersonatedUserId) {
        UserAuthUrls userAuthUrls = usersRepository.getUserAuthUrls(userId, false, impersonatedUserId);
        return UserConverter.fromMsEntity(userAuthUrls);
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        getUserAndCheck(userId);
        return UserConverter.fromMsEntity(usersRepository.getUserNotifications(userId));
    }

    public void setUserNotifications(Long userId, List<NotificationDTO> notifications) {
        getUserAndCheck(userId);
        usersRepository.setUserNotifications(userId, UserConverter.toMsEntity(notifications));
    }

    public UserSecretDTO refreshApiKey(Long userId) {
        getUserAndCheck(userId);
        return usersRepository.refreshApiKey(userId);
    }

    public MFAResponseDTO sendMFAActivationEmail(MFARequestDTO request) {
        MFARequest mfa = fillMFARequest(request);
        MFAResponse status = usersRepository.sendMFAActivationEmail(SecurityUtils.getUserId(), mfa);
        return buildMFAResponseDTO(status);
    }

    public MFAResponseDTO validateAndActivateMFA(MFARequestDTO request) {
        MFARequest mfa = fillMFARequest(request);
        MFAResponse status = usersRepository.validateAndActivateMFA(SecurityUtils.getUserId(), mfa);
        return buildMFAResponseDTO(status);
    }

    public void updateUserVisibility(Long userId, UpdateVisibilityDTO request) {
        getUserAndCheck(userId);
        usersRepository.updateUserVisibility(userId, request);
    }

    private MFARequest fillMFARequest(MFARequestDTO request) {
        var user = getAuthUser();
        MFARequest mfaRequest = new MFARequest();
        mfaRequest.setUsername(user.getUsername());
        mfaRequest.setPassword(request.password());
        MFA mfa = new MFA();
        mfa.setType(request.mfa().getType());
        if (request.mfa().getCode() != null) {
            mfa.setCode(request.mfa().getCode());
        }
        mfaRequest.setMfa(mfa);
        mfaRequest.setOperator(user.getOperator().getShortName());
        return mfaRequest;
    }

    private MFAResponseDTO buildMFAResponseDTO(MFAResponse status) {
        if (MFAState.SUCCESS.equals(status.getState())) {
            return buildSuccessMFAResponse();
        } else {
            return buildFailMFAResponse();
        }
    }

    private MFAResponseDTO buildSuccessMFAResponse() {
        MFAResponseDTO response = new MFAResponseDTO();
        response.setState(MFAState.SUCCESS);
        return response;
    }

    private MFAResponseDTO buildFailMFAResponse() {
        MFAResponseDTO response = new MFAResponseDTO();
        response.setState(MFAState.FAIL);
        response.setMessage(INVALID_CREDENTIALS);
        return response;
    }

    private void fillReportsInfo(UserSelfDTO userDTO, User user) {
        Boolean supersetUser = usersRepository.isSupersetUser(user.getId());
        if (BooleanUtils.isNotTrue(supersetUser)) {
            UserAuthUrls userAuthUrls = usersRepository.getUserAuthUrls(user.getId(), false, null);
            if (userAuthUrls != null) {
                Boolean mstrUserHasSubscriptions = usersRepository.userHasSubscriptions(user.getId());
                Boolean canImpersonate = usersRepository.userCanImpersonate(user.getId());
                UserReportsDTO reportsDTO = new UserReportsDTO();
                reportsDTO.setLogin(userAuthUrls.getLogin());
                reportsDTO.setLogout(userAuthUrls.getLogout());
                reportsDTO.setLoad(userAuthUrls.getLoad());
                reportsDTO.setMstrUserHasSubscriptions(mstrUserHasSubscriptions);
                Boolean canImpersonateNull = Boolean.TRUE.equals(canImpersonate) ? canImpersonate : null;
                reportsDTO.setCanImpersonate(canImpersonateNull);
                userDTO.setReports(reportsDTO);
            }
        } else {
            UserReportsDTO reportsDTO = new UserReportsDTO();
            reportsDTO.setSuperset(true);
            userDTO.setReports(reportsDTO);
        }
    }

    private <T extends BaseUserDTO> void fillLocationIds(User userDTO, T user) {
        UserLocationDTO location = user.getLocation();
        if (location != null) {
            if (location.getCountry() != null && location.getCountry().getCode() != null) {
                userDTO.setCountryId(masterdataService.getCountryIdByCode(location.getCountry().getCode()));
            }
            if (location.getCountrySubdivision() != null && location.getCountrySubdivision().getCode() != null) {
                userDTO.setCountrySubdivisionId(masterdataService.getCountrySubdivisionIdByCode(location.getCountrySubdivision().getCode()));
            }
        }
    }

    private <T extends BaseUserDTO> void fillLanguage(User userDTO, T user, Entity entity) {
        if (user.getLanguage() != null) {
            userDTO.setLanguageId(masterdataService.getLanguageByCode(ConverterUtils.toLocale(user.getLanguage())));
        } else if (entity != null) {
            userDTO.setLanguageId(entity.getLanguage().getId().intValue());
        }
    }

    private UserSelfDTO fillUserSelfDTO(User user, UserSelfDTO userDTO) {
        Entity entity = entitiesRepository.getCachedEntity(user.getEntityId());
        Map<Long, String> languages = masterdataService.getLanguagesByIds();
        UserConverter.fromMsEntityDetails(user, userDTO, EntityConverter.fromMsEntity(entity, languages));
        EntityConverter.fillEntityAdmin(userDTO.getEntity(), entitiesRepository::getEntities);
        fillEntityDTO(entity, userDTO.getEntity());
        fillUserDTO(user, entity, userDTO);

        return userDTO;
    }

    private UserResponseDTO fillUserResponseDTO(User user, UserResponseDTO userDTO) {
        Entity entity = entitiesRepository.getCachedEntity(user.getEntityId());

        UserConverter.fromMsEntityReduced(user, userDTO, EntityConverter.fromMsEntityReduced(entity));
        fillUserDTO(user, entity, userDTO);

        return  userDTO;
    }

    private void fillUserDTO(User user, Entity entity, UserDTO userDTO){
        userDTO.setTimezone(entity.getTimezone().getValue());
        Operator operator = cacheRepository.cached(CACHE_MYSELF_OPERATOR, TTL, TimeUnit.SECONDS, () ->
                entitiesRepository.getOperator(entity.getOperator().getId()), new Object[]{entity.getOperator().getId()});

        userDTO.setUseMulticurrency(operator.getUseMultiCurrency());
        if (BooleanUtils.isTrue(operator.getUseMultiCurrency())) {
            userDTO.setCurrency(operator.getCurrencies().getDefaultCurrency());
        } else {
            userDTO.setCurrency(entity.getCurrency().getValue());
        }
        userDTO.setApikey(maskApiKey(user.getApiKey()));
        userDTO.setMfaType(user.getMfaType());
        userDTO.setVisibility(OperatorsConverter.toDTO(user.getVisibility()));

        userDTO.setOperator(OperatorsConverter.toDTO(operator));

        if (user.getLanguageId() != null) {
            MasterdataValue language = masterdataService.getLanguage(user.getLanguageId().longValue());
            userDTO.setLanguage(ConverterUtils.toLanguageTag(language.getCode()));
        }
        if (user.getCountryId() != null) {
            MasterdataValue country = masterdataService.getCountry(user.getCountryId().longValue());
            userDTO.getLocation().setCountry(new CodeNameDTO(country.getCode(), country.getName()));
        }
        if (user.getCountrySubdivisionId() != null) {
            MasterdataValue province = masterdataService.getCountrySubdivision(user.getCountrySubdivisionId().longValue());
            userDTO.getLocation().setCountrySubdivision(new CodeNameDTO(province.getCode(), province.getName()));
        }
    }

    private void setPassword(Long userId, String password, String token) {
        User user = new User();
        user.setId(userId);
        user.setPassword(password);
        Optional.ofNullable(token).ifPresent(user::setToken);
        usersRepository.update(user);
    }

    private void getUserAndCheck(Long userId) {
        User user = null;
        try {
            user = usersRepository.getById(userId);
        } catch (HttpErrorException e) {
            if (!e.getHttpStatus().equals(HttpStatus.NOT_FOUND)) {
                throw e;
            }
        }
        if (user != null
            && !user.getId().equals(SecurityUtils.getUserId())
            && !SecurityUtils.accessibleResource(user.getEntityId(), user.getOperatorId(), ROLE_OPR_MGR, ROLE_ENT_MGR)) {
                throw new OneboxRestException(FORBIDDEN_RESOURCE);
        }
    }


    private void fillEntityDTO(Entity entity, EntityDTO entityDTO) {
        if (entity.getCountryId() != null) {
            MasterdataValue country = masterdataService.getCountry(entity.getCountryId().longValue());
            entityDTO.getContact().setCountry(new CodeNameDTO(country.getCode(), null));
        }
        if (entity.getCountrySubdivisionId() != null) {
            MasterdataValue country = masterdataService.getCountrySubdivision(entity.getCountrySubdivisionId().longValue());
            entityDTO.getContact().setCountrySubdivision(new CodeNameDTO(country.getCode(), null));
        }
        if (entity.getInvoiceCountryId() != null) {
            MasterdataValue country = masterdataService.getCountry(entity.getInvoiceCountryId().longValue());
            entityDTO.getInvoiceData().setCountry(new CodeNameDTO(country.getCode(), null));
        }
        if (entity.getInvoiceCountrySubdivisionId() != null) {
            MasterdataValue country = masterdataService.getCountrySubdivision(entity.getInvoiceCountrySubdivisionId().longValue());
            entityDTO.getInvoiceData().setCountrySubdivision(new CodeNameDTO(country.getCode(), null));
        }
        if ((entity.getCustomization() == null || BooleanUtils.isNotTrue(entity.getCustomization().getEnabled())) &&
                (entity.getOperator() != null && entity.getOperator().getCustomization() != null && BooleanUtils.isTrue(entity.getOperator().getCustomization().getEnabled()))) {
            if (entityDTO.getSettings() == null) {
                entityDTO.setSettings(new EntitySettingsDTO());
            }
            SettingsCustomizationDTO customization = new SettingsCustomizationDTO();
            customization.setEnabled(entity.getOperator().getCustomization().getEnabled());
            customization.setFaviconUrl(entity.getOperator().getCustomization().getFaviconUrl());
            customization.setLogoUrl(entity.getOperator().getCustomization().getLogoUrl());
            customization.setTinyUrl(entity.getOperator().getCustomization().getTinyUrl());
            entityDTO.getSettings().setCustomization(customization);
        }
    }

    private static boolean userLoggedButOtherOperator(User userToModify) {
        return SecurityUtils.authenticatedUser()
                && !userToModify.getOperatorId().equals(SecurityUtils.getUserOperatorId());
    }
    
    private static String maskApiKey(String apiKey) {
        if (SecurityUtils.hasAnyRole(ROLE_OPR_MGR, ROLE_SYS_MGR, ROLE_OPR_ANS, ROLE_SYS_ANS)) {
            return apiKey;
        }
        return apiKey.replaceAll(".(?=.{5})", "*");
      }

    private void fillProducerDTO(UserDTO userDTO, Long producerId) {
        if (producerId != null) {
            Producer producer = entitiesRepository.getProducer(producerId);
            if (producer != null) {
                userDTO.setProducer(new ProducerDTO());
                userDTO.getProducer().setId(producer.getId());
                userDTO.getProducer().setName(producer.getName());
            }
        }
    }
}
