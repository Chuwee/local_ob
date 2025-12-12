package es.onebox.service;

import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.dto.Operator;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.dto.Users;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.fever.dto.FvUserAuth;
import es.onebox.fever.service.AuthUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthUserServiceTest {

    private static final String API_KEY = "abc123";
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "a@b.c";
    private static final Long ENTITY_ID = 2L;
    private static final Long OPERATOR_ID = 3L;

    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private AuthUserService authUserService;

    private MockedStatic<AuthenticationUtils> authenticationUtils;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        authenticationUtils = Mockito.mockStatic(AuthenticationUtils.class);
    }

    @AfterEach
    public void tearDown() {
        authenticationUtils.close();
    }

    @Test
    public void getUserInfo_ok() {
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setApiKey(API_KEY);
        authenticationData.setEntityId(ENTITY_ID);

        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setAllowFeverZone(Boolean.TRUE);
        entity.setExternalReference("123");
        entity.setOperator(new Operator());
        entity.getOperator().setId(OPERATOR_ID);

        Operator operator = new Operator();
        operator.setId(OPERATOR_ID);
        operator.setAllowFeverZone(Boolean.TRUE);

        User user = new User();
        user.setApiKey(API_KEY);
        user.setEmail(USERNAME);
        user.setId(USER_ID);
        user.setExternalReference("123");

        Users users = new Users();
        users.setData(new ArrayList<>());
        users.getData().add(user);

        authenticationUtils.when(AuthenticationUtils::getAuthDataOrNull).thenReturn(authenticationData);
        when(entitiesRepository.getByIdCached(ENTITY_ID)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(OPERATOR_ID)).thenReturn(operator);
        when(usersRepository.getFilteredUsers(any())).thenReturn(users);

        FvUserAuth userAuth = authUserService.getUserInfo(ENTITY_ID);
        assertEquals(user.getId(), userAuth.getId());
        assertEquals(Integer.valueOf(user.getExternalReference()), userAuth.getFvId());
        assertEquals(user.getEmail(), userAuth.getEmail());
        assertEquals(entity.getId(), userAuth.getObEntityId());
        assertEquals(Integer.valueOf(user.getExternalReference()), userAuth.getFvPartnerId());
    }

    @Test
    public void getUserInfo_noUserFound_ko() {
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setApiKey(API_KEY);
        authenticationData.setEntityId(ENTITY_ID);

        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setAllowFeverZone(Boolean.TRUE);
        entity.setExternalReference("123");
        entity.setOperator(new Operator());
        entity.getOperator().setId(OPERATOR_ID);

        Operator operator = new Operator();
        operator.setId(OPERATOR_ID);
        operator.setAllowFeverZone(Boolean.TRUE);

        Users users = new Users();
        users.setData(new ArrayList<>());

        authenticationUtils.when(AuthenticationUtils::getAuthDataOrNull).thenReturn(authenticationData);
        when(entitiesRepository.getByIdCached(ENTITY_ID)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(OPERATOR_ID)).thenReturn(operator);
        when(usersRepository.getFilteredUsers(any())).thenReturn(users);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> authUserService.getUserInfo(ENTITY_ID));
        assertEquals(ApiExternalErrorCode.USER_NOT_FOUND.name(), ex.getErrorCode());
    }

    @Test
    public void getUserInfo_multipleUsers_ko() {
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setApiKey(API_KEY);
        authenticationData.setEntityId(ENTITY_ID);

        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setAllowFeverZone(Boolean.TRUE);
        entity.setExternalReference("123");
        entity.setOperator(new Operator());
        entity.getOperator().setId(OPERATOR_ID);

        Operator operator = new Operator();
        operator.setId(OPERATOR_ID);
        operator.setAllowFeverZone(Boolean.TRUE);

        User user1 = new User();
        User user2 = new User();

        Users users = new Users();
        users.setData(List.of(user1, user2));

        authenticationUtils.when(AuthenticationUtils::getAuthDataOrNull).thenReturn(authenticationData);
        when(entitiesRepository.getByIdCached(ENTITY_ID)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(OPERATOR_ID)).thenReturn(operator);
        when(usersRepository.getFilteredUsers(any())).thenReturn(users);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> authUserService.getUserInfo(ENTITY_ID));
        assertEquals(ApiExternalErrorCode.USER_NOT_FOUND.name(), ex.getErrorCode());
    }

    @Test
    public void getUserInfo_noUserExternal_ko() {
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setApiKey(API_KEY);
        authenticationData.setEntityId(ENTITY_ID);

        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setAllowFeverZone(Boolean.TRUE);
        entity.setExternalReference("123");
        entity.setOperator(new Operator());
        entity.getOperator().setId(OPERATOR_ID);

        Operator operator = new Operator();
        operator.setId(OPERATOR_ID);
        operator.setAllowFeverZone(Boolean.TRUE);

        User user = new User();
        user.setApiKey(API_KEY);
        user.setEmail(USERNAME);
        user.setId(USER_ID);

        Users users = new Users();
        users.setData(new ArrayList<>());
        users.getData().add(user);

        authenticationUtils.when(AuthenticationUtils::getAuthDataOrNull).thenReturn(authenticationData);
        when(entitiesRepository.getByIdCached(ENTITY_ID)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(OPERATOR_ID)).thenReturn(operator);
        when(usersRepository.getFilteredUsers(any())).thenReturn(users);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> authUserService.getUserInfo(ENTITY_ID));
        assertEquals(ApiExternalErrorCode.FV_ZONE_NOT_AVAILABLE.name(), ex.getErrorCode());
    }

    @Test
    public void getUserInfo_noOperator_ko() {
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setApiKey(API_KEY);
        authenticationData.setEntityId(ENTITY_ID);

        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setAllowFeverZone(Boolean.TRUE);
        entity.setExternalReference("123");
        entity.setOperator(new Operator());
        entity.getOperator().setId(OPERATOR_ID);

        authenticationUtils.when(AuthenticationUtils::getAuthDataOrNull).thenReturn(authenticationData);
        when(entitiesRepository.getByIdCached(ENTITY_ID)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(OPERATOR_ID)).thenReturn(null);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> authUserService.getUserInfo(ENTITY_ID));
        assertEquals(ApiExternalErrorCode.FV_ZONE_NOT_AVAILABLE.name(), ex.getErrorCode());
    }

    @Test
    public void getUserInfo_noOperatorAllowFvZone_ko() {
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setApiKey(API_KEY);
        authenticationData.setEntityId(ENTITY_ID);

        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setAllowFeverZone(Boolean.TRUE);
        entity.setExternalReference("123");
        entity.setOperator(new Operator());
        entity.getOperator().setId(OPERATOR_ID);

        Operator operator = new Operator();
        operator.setId(OPERATOR_ID);
        operator.setAllowFeverZone(Boolean.FALSE);

        authenticationUtils.when(AuthenticationUtils::getAuthDataOrNull).thenReturn(authenticationData);
        when(entitiesRepository.getByIdCached(ENTITY_ID)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(OPERATOR_ID)).thenReturn(null);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> authUserService.getUserInfo(ENTITY_ID));
        assertEquals(ApiExternalErrorCode.FV_ZONE_NOT_AVAILABLE.name(), ex.getErrorCode());
    }

    @Test
    public void getUserInfo_noEntity_ko() {
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setApiKey(API_KEY);
        authenticationData.setEntityId(ENTITY_ID);

        authenticationUtils.when(AuthenticationUtils::getAuthDataOrNull).thenReturn(authenticationData);
        when(entitiesRepository.getByIdCached(ENTITY_ID)).thenReturn(null);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> authUserService.getUserInfo(ENTITY_ID));
        assertEquals(ApiExternalErrorCode.ENTITY_NOT_FOUND.name(), ex.getErrorCode());
    }

    @Test
    public void getUserInfo_noEntityExternal_ko() {
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setApiKey(API_KEY);
        authenticationData.setEntityId(ENTITY_ID);

        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setAllowFeverZone(Boolean.TRUE);

        authenticationUtils.when(AuthenticationUtils::getAuthDataOrNull).thenReturn(authenticationData);
        when(entitiesRepository.getByIdCached(ENTITY_ID)).thenReturn(entity);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> authUserService.getUserInfo(ENTITY_ID));
        assertEquals(ApiExternalErrorCode.FV_ZONE_NOT_AVAILABLE.name(), ex.getErrorCode());
    }
}
