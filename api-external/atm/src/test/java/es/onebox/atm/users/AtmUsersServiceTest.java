package es.onebox.atm.users;

import es.onebox.atm.users.dto.ATMOauthResponse;
import es.onebox.atm.users.dto.ATMUserPromotion;
import es.onebox.atm.users.dto.ATMUserPromotionDTO;
import es.onebox.atm.users.repository.ATMPromotionsRepository;
import es.onebox.atm.users.service.AtmUsersService;
import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.datasources.distribution.dto.OrderResponse;
import es.onebox.common.datasources.distribution.dto.PresalesRequest;
import es.onebox.common.datasources.distribution.repository.DistributionRepository;
import es.onebox.common.datasources.ms.client.dto.AuthVendorChannelConfig;
import es.onebox.common.datasources.ms.client.dto.AuthVendorConfig;
import es.onebox.common.datasources.ms.client.dto.AuthVendorUserData;
import es.onebox.common.datasources.ms.client.repository.AuthVendorChannelConfigRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.exception.OneboxRestException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static es.onebox.utils.ObjectRandomizer.random;
import static es.onebox.utils.ObjectRandomizer.randomString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


class AtmUsersServiceTest {

    @InjectMocks
    private AtmUsersService atmUsersService;

    @Mock
    private AuthVendorChannelConfigRepository authVendorChannelConfigRepository;

    @Mock
    private ATMPromotionsRepository atmPromotionsRepository;

    @Mock
    private DistributionRepository distributionRepository;

    private static MockedStatic<AuthenticationUtils> authenticationUtils;

    private static MockedStatic<AuthContextUtils> authContextUtils;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void beforeAll() {
        authenticationUtils = Mockito.mockStatic(AuthenticationUtils.class);
        authContextUtils = Mockito.mockStatic(AuthContextUtils.class);
    }

    @AfterAll
    public static void afterAll() {
        authenticationUtils.close();
        authContextUtils.close();
    }

    @Test
    void getRelatedUsers() {
        Mockito.when(AuthContextUtils.getToken()).thenReturn("sdfgsdfsdf");

        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setChannelId(4L);
        Mockito.when(AuthenticationUtils.getAuthDataOrNull()).thenReturn(authenticationData);

        AuthVendorChannelConfig authVendorChannelConfig = new AuthVendorChannelConfig();
        List<String> vendors = new ArrayList<>();
        vendors.add("234234");
        authVendorChannelConfig.setVendors(vendors);
        Mockito.when(authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(Mockito.anyLong())).thenReturn(authVendorChannelConfig);

        Mockito.when(authVendorChannelConfigRepository.getRelatdUsers(Mockito.anyString(), any())).thenReturn(null);

        try {
            atmUsersService.getRelatedUsers(randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        AuthVendorUserData authVendorUserData = random(AuthVendorUserData.class);
        List<AuthVendorUserData> userData = List.of(authVendorUserData);
        Mockito.when(authVendorChannelConfigRepository.getRelatdUsers(Mockito.anyString(), any())).thenReturn(userData);

        List<AuthVendorUserData> result = atmUsersService.getRelatedUsers(randomString());
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void getPromotions() {
        Mockito.when(AuthContextUtils.getToken()).thenReturn("sdfgsdfsdf");

        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setChannelId(4L);
        Mockito.when(AuthenticationUtils.getAuthDataOrNull()).thenReturn(authenticationData);

        AuthVendorChannelConfig authVendorChannelConfig = new AuthVendorChannelConfig();
        List<String> vendors = new ArrayList<>();
        vendors.add("234234");
        authVendorChannelConfig.setVendors(vendors);
        Mockito.when(authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(Mockito.anyLong())).thenReturn(authVendorChannelConfig);

        AuthVendorConfig authVendorConfig = random(AuthVendorConfig.class);
        authVendorConfig.getProperties().put("atm_api_oauth_url", randomString());
        authVendorConfig.getProperties().put("atm_api_client_id", randomString());
        authVendorConfig.getProperties().put("atm_api_client_secret", randomString());
        authVendorConfig.getProperties().put("atm_api_domain", randomString());
        authVendorConfig.getProperties().put("promotions_api_path", randomString());
        authVendorConfig.getProperties().put("promotions_api_path_params", randomString());
        Mockito.when(authVendorChannelConfigRepository.getAuthVendorConfiguration(Mockito.anyString())).thenReturn(authVendorConfig);

        ATMOauthResponse atmOauthResponse = random(ATMOauthResponse.class);
        Mockito.when(atmPromotionsRepository.login(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(atmOauthResponse);

        List<ATMUserPromotion> userPromotions = ObjectRandomizer.randomListOf(ATMUserPromotion.class, 2);
        userPromotions.get(0).setDiscountType("Numerico");
        userPromotions.get(0).setPromotionType("Tarjeta monedero");
        userPromotions.get(0).setStatus("");
        Mockito.when(atmPromotionsRepository.getUserPromotions(Mockito.anyString(), any(), Mockito.anyString(), Mockito.anyString())).thenReturn(userPromotions);

        List<ATMUserPromotionDTO> result = atmUsersService.getUserPromotions(randomString());

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void validateMemberPresale() {
        String userSalesforceId = "atm_user_test";
        String orderId = "order456";
        Long sessionId = 789L;
        Long channelId = 10L;
        String channelOauthToken = AuthContextUtils.getToken();

        AuthenticationData mockAuthData = new AuthenticationData();
        mockAuthData.setChannelId(channelId);
        authenticationUtils.when(AuthenticationUtils::getAuthDataOrNull).thenReturn(mockAuthData);

        AuthVendorChannelConfig mockChannelConfig = new AuthVendorChannelConfig();
        mockChannelConfig.setVendors(List.of("vendorTest"));
        when(authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(channelId)).thenReturn(mockChannelConfig);

        Map<String, Object> additionalData = Map.of(
                "membership_id", "AVET123",
                "avet_pin", "PIN456",
                "classification", "SUBSCRIBER_MEMBER"
        );
        AuthVendorUserData mockUserData = new AuthVendorUserData();
        mockUserData.setAdditionalData(additionalData);
        when(authVendorChannelConfigRepository.getUserData(eq("vendorTest"), anyMap())).thenReturn(mockUserData);

        OrderResponse expectedResponse = new OrderResponse();
        when(distributionRepository.validatePresales(eq(channelOauthToken), any(PresalesRequest.class), eq(orderId))).thenReturn(expectedResponse);

        OrderResponse actualResponse = atmUsersService.validateMemberPresale(userSalesforceId, orderId, sessionId);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void authDataIsNull() {
        authenticationUtils.when(AuthenticationUtils::getAuthDataOrNull).thenReturn(null);

        OneboxRestException ex = assertThrows(
                OneboxRestException.class,
                () -> atmUsersService.validateMemberPresale("user", "order", 1L)
        );

        assertEquals(ApiExternalErrorCode.CHANNEL_ID_NOT_FOUND.name(), ex.getErrorCode());
    }

    @Test
    void vendorListIsEmpty() {
        Long channelId = 1L;
        AuthenticationData authData = new AuthenticationData();
        authData.setChannelId(channelId);
        authenticationUtils.when(AuthenticationUtils::getAuthDataOrNull).thenReturn(authData);

        AuthVendorChannelConfig config = new AuthVendorChannelConfig();
        config.setVendors(Collections.emptyList());
        when(authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(channelId)).thenReturn(config);

        OneboxRestException ex = assertThrows(
                OneboxRestException.class,
                () -> atmUsersService.validateMemberPresale("user", "order", 1L)
        );

        assertEquals(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG.name(), ex.getErrorCode());
    }

    @Test
    void getUserFieldNotFound() {
        AuthVendorUserData userData = new AuthVendorUserData();
        userData.setAdditionalData(Collections.emptyMap());

        OneboxRestException ex = assertThrows(
                OneboxRestException.class,
                () -> atmUsersService.getUserField(userData, "membership_id")
        );

        assertEquals(ApiExternalErrorCode.USER_NOT_FOUND.name(), ex.getErrorCode());
    }
}
