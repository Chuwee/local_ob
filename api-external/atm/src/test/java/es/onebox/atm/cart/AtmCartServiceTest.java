package es.onebox.atm.cart;

import es.onebox.atm.cart.dto.ATMFriendCodeCartRequest;
import es.onebox.atm.cart.dto.ATMMemberCartRequest;
import es.onebox.atm.cart.enums.ATMClassification;
import es.onebox.atm.cart.service.AtmCartService;
import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.datasources.dispatcher.dto.PartnerInfoResponse;
import es.onebox.common.datasources.dispatcher.repositoty.IntAvetDispatcherRepository;
import es.onebox.common.datasources.ms.client.dto.AuthVendorCallbackValidation;
import es.onebox.common.datasources.ms.client.dto.AuthVendorChannelConfig;
import es.onebox.common.datasources.ms.client.dto.AuthVendorConfig;
import es.onebox.common.datasources.ms.client.dto.AuthVendorUserData;
import es.onebox.common.datasources.ms.client.repository.AuthVendorChannelConfigRepository;
import es.onebox.common.datasources.rest.dto.ns.data_query.prices.ActivityTicketTypeAvailability;
import es.onebox.common.datasources.rest.dto.ns.data_query.prices.ActivityTicketTypesAvailability;
import es.onebox.common.datasources.rest.dto.ns.data_query.session.SessionInfo;
import es.onebox.common.datasources.rest.dto.ns.shopping.cart.UserApplicableGroup;
import es.onebox.common.datasources.rest.repository.ShoppingCartRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import es.onebox.utils.ObjectRandomizer;
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

import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


class AtmCartServiceTest {

    private static final String ATM_VENDOR_ID = "ATMIDENTITY";
    private static final String CORRECT_DISCOUNT_TYPE = "ADULTO_JUVENIL";


    @InjectMocks
    private AtmCartService atmCartService;

    @Mock
    private AuthVendorChannelConfigRepository authVendorChannelConfigRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private IntAvetDispatcherRepository intAvetDispatcherRepository;

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
    void registerFootballMemberDiscountTest() throws Exception {
        when(AuthContextUtils.getToken()).thenReturn("kjashdkasdkjasdk");
        // atmCartRequest null
        try {
            final String cartToken = "kfuisdf";
            atmCartService.registerFootballMemberDiscount(cartToken, null);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.SHOPPING_CART_TOKEN_MANDATORY.getErrorCode());
        }

        ATMMemberCartRequest atmCartRequest = new ATMMemberCartRequest();
        atmCartRequest.setMemberId("sdfvsvcv");
        atmCartRequest.setSessionId(4L);
        // Cart token null
        try {
            final String emptyCartToken = null;
            atmCartService.registerFootballMemberDiscount(emptyCartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.SHOPPING_CART_TOKEN_MANDATORY.getErrorCode());
        }

        doNothing().when(shoppingCartRepository).validateCart(anyString());
        AuthVendorChannelConfig authVendorChannelConfig = new AuthVendorChannelConfig();
        List<String> vendors = new ArrayList<>();
        vendors.add("234234");
        authVendorChannelConfig.setVendors(vendors);
        when(authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(anyLong())).thenReturn(authVendorChannelConfig);

        // User data null
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(null);
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setChannelId(4L);
        authenticationData.setEntityId(1L);
        when(AuthenticationUtils.getAuthDataOrNull()).thenReturn(authenticationData);
        final String cartToken = "sdfsdf";
        try {
            atmCartService.registerFootballMemberDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        // User data additional data null
        vendors.add(ATM_VENDOR_ID);
        authVendorChannelConfig.setVendors(vendors);
        when(authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(anyLong())).thenReturn(authVendorChannelConfig);
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(null);

        try {
            atmCartService.registerFootballMemberDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        // User data empty additional data
        AuthVendorUserData authVendorUserData = getAuthVendorUserData();
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(authVendorUserData);

        try {
            atmCartService.registerFootballMemberDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        // Additional data user membership data == null
        authVendorUserData.setAdditionalData(getEmptyAdditionalData());
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(authVendorUserData);
        try {
            atmCartService.registerFootballMemberDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        Map<String, Object> additionalData = getNoClassificationAdditionalData();
        authVendorUserData.setAdditionalData(additionalData);
        try {
            atmCartService.registerFootballMemberDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_CREDENTIALS.getErrorCode(), e.getErrorCode());
        }

        additionalData.put(ATMVendorConstants.CLASSIFICATION, ATMClassification.MEMBER.name());

        try {
            atmCartService.registerFootballMemberDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG.getErrorCode(), e.getErrorCode());
        }

        //No callback validation Id (id colectivo)
        AuthVendorConfig authVendorConfig = new AuthVendorConfig();
        AuthVendorCallbackValidation authVendorCallbackValidation = new AuthVendorCallbackValidation();
        authVendorConfig.setCallbackValidation(authVendorCallbackValidation);
        when(authVendorChannelConfigRepository.getAuthVendorConfiguration(anyString())).thenReturn(authVendorConfig);
        try {
            atmCartService.registerFootballMemberDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG.getErrorCode(), e.getErrorCode());
        }

        //Fail on empty partner info response
        authVendorCallbackValidation.setId(5L);
        PartnerInfoResponse partnerInfoResponse = null;
        when(intAvetDispatcherRepository.getPartnerInformation(anyLong(), anyString(), anyString(), anyInt())).thenReturn(partnerInfoResponse);
        try {
            atmCartService.registerFootballMemberDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        //Fail on partner info with wrong permission
        partnerInfoResponse = new PartnerInfoResponse();
        partnerInfoResponse.setIdPermiso(253);
        when(intAvetDispatcherRepository.getPartnerInformation(anyLong(), anyString(), anyString(), anyInt())).thenReturn(partnerInfoResponse);
        try {
            atmCartService.registerFootballMemberDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.BLOCKED_USER.getErrorCode(), e.getErrorCode());
        }

        partnerInfoResponse.setIdPermiso(254);
        atmCartService.registerFootballMemberDiscount(cartToken, atmCartRequest);
    }

    @Test
    void addTourMemberTicketTest() throws Exception {
        when(AuthContextUtils.getToken()).thenReturn("entityId");
        // atmCartRequest null
        try {
            final String cartToken = "fjlkfjgkls";
            atmCartService.addTourMemberTicket(cartToken, null);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.SHOPPING_CART_TOKEN_MANDATORY.getErrorCode());
        }

        ATMMemberCartRequest atmCartRequest = new ATMMemberCartRequest();
        atmCartRequest.setSessionId(4L);
        // Cart token null
        try {
            String emptyCartToken = null;
            atmCartService.addTourMemberTicket(emptyCartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.SHOPPING_CART_TOKEN_MANDATORY.getErrorCode());
        }

        doNothing().when(shoppingCartRepository).validateCart(anyString());
        AuthVendorChannelConfig authVendorChannelConfig = new AuthVendorChannelConfig();
        List<String> vendors = new ArrayList<>();
        vendors.add("234234");
        authVendorChannelConfig.setVendors(vendors);
        when(authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(anyLong())).thenReturn(authVendorChannelConfig);
        final String cartToken = "sdfsdf";
        // friend code null
        try {
            atmCartService.addTourMemberTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.MEMBER_ID_MANDATORY.getErrorCode());
        }

        atmCartRequest.setMemberId("");
        try {
            atmCartService.addTourMemberTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.MEMBER_ID_MANDATORY.getErrorCode());
        }

        atmCartRequest.setMemberId("ABCD");
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(null);
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setChannelId(4L);
        authenticationData.setEntityId(1L);
        when(AuthenticationUtils.getAuthDataOrNull()).thenReturn(authenticationData);

        try {
            atmCartService.addTourMemberTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        // User data additional data null
        vendors.add(ATM_VENDOR_ID);
        authVendorChannelConfig.setVendors(vendors);
        when(authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(anyLong())).thenReturn(authVendorChannelConfig);
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(null);

        try {
            atmCartService.addTourMemberTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        // User data empty additional data
        AuthVendorUserData authVendorUserData = getAuthVendorUserData();
        authVendorUserData.setAdditionalData(getEmptyAdditionalData());
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(authVendorUserData);

        try {
            atmCartService.addTourMemberTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        //Additional data no classification
        Map<String, Object> additionalData = getNoClassificationAdditionalData();
        authVendorUserData.setAdditionalData(additionalData);
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(authVendorUserData);
        try {
            atmCartService.addTourMemberTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_CREDENTIALS.getErrorCode(), e.getErrorCode());
        }

        additionalData.put(ATMVendorConstants.CLASSIFICATION, ATMClassification.MEMBER.name());
        try {
            atmCartService.addTourMemberTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        SessionInfo sessionInfo = getSessionInfo();
        when(shoppingCartRepository.getSessionInfo(anyLong())).thenReturn(sessionInfo);

        AuthVendorConfig authVendorConfig = new AuthVendorConfig();
        AuthVendorCallbackValidation authVendorCallbackValidation = new AuthVendorCallbackValidation();
        authVendorCallbackValidation.setId(5L);
        authVendorConfig.setCallbackValidation(authVendorCallbackValidation);

        when(authVendorChannelConfigRepository.getAuthVendorConfiguration(anyString())).thenReturn(authVendorConfig);
        UserApplicableGroup userApplicableGroup = ObjectRandomizer.random(UserApplicableGroup.class);
        when(shoppingCartRepository.getUserGroupValidation(anyString(), anyLong(), anyString(), anyString(), anyLong())).thenReturn(userApplicableGroup);

        //Fail on partner info with wrong permission
        PartnerInfoResponse partnerInfoResponse = new PartnerInfoResponse();
        partnerInfoResponse.setIdPermiso(253);
        when(intAvetDispatcherRepository.getPartnerInformation(anyLong(), anyString(), anyString(), anyInt())).thenReturn(partnerInfoResponse);
        try {
            atmCartService.addTourMemberTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.BLOCKED_USER.getErrorCode(), e.getErrorCode());
        }

        partnerInfoResponse.setIdPermiso(254);
        atmCartService.addTourMemberTicket(cartToken, atmCartRequest);
    }

    @Test
    void registerFootballFriendDiscountTest() throws Exception {
        String entityId = "entityId";
        when(AuthContextUtils.getToken()).thenReturn(entityId);
        // atmCartRequest null
        try {
            final String cartToken = "kfuisdf";
            atmCartService.registerFootballFriendDiscount(cartToken, null);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.SHOPPING_CART_TOKEN_MANDATORY.getErrorCode());
        }

        ATMFriendCodeCartRequest atmCartRequest = new ATMFriendCodeCartRequest();
        atmCartRequest.setCode("sdfvsvcv");
        atmCartRequest.setSessionId(4L);
        // Cart token null
        try {
            final String emptyCartToken = null;
            atmCartService.registerFootballFriendDiscount(emptyCartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.SHOPPING_CART_TOKEN_MANDATORY.getErrorCode());
        }

        doNothing().when(shoppingCartRepository).validateCart(anyString());
        AuthVendorChannelConfig authVendorChannelConfig = new AuthVendorChannelConfig();
        List<String> vendors = new ArrayList<>();
        vendors.add("234234");
        authVendorChannelConfig.setVendors(vendors);
        when(authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(anyLong())).thenReturn(authVendorChannelConfig);

        // User data null
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(null);
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setChannelId(4L);
        authenticationData.setEntityId(1L);
        when(AuthenticationUtils.getAuthDataOrNull()).thenReturn(authenticationData);
        final String cartToken = "sdfsdf";
        try {
            atmCartService.registerFootballFriendDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        // User data additional data null
        vendors.add(ATM_VENDOR_ID);
        authVendorChannelConfig.setVendors(vendors);
        when(authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(anyLong())).thenReturn(authVendorChannelConfig);
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(null);

        try {
            atmCartService.registerFootballFriendDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        // User data empty additional data
        AuthVendorUserData authVendorUserData = getAuthVendorUserData();
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(authVendorUserData);

        try {
            atmCartService.registerFootballFriendDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        // Additional data user membership data == null
        authVendorUserData.setAdditionalData(getEmptyAdditionalData());
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(authVendorUserData);
        try {
            atmCartService.registerFootballFriendDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        Map<String, Object> additionalData = getNoClassificationAdditionalData();
        authVendorUserData.setAdditionalData(additionalData);
        try {
            atmCartService.registerFootballFriendDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_CREDENTIALS.getErrorCode(), e.getErrorCode());
        }

        additionalData.put(ATMVendorConstants.CLASSIFICATION, ATMClassification.MEMBER.name());

        try {
            atmCartService.registerFootballFriendDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG.getErrorCode(), e.getErrorCode());
        }

        //No callback validation Id (id colectivo)
        AuthVendorConfig authVendorConfig = new AuthVendorConfig();
        AuthVendorCallbackValidation authVendorCallbackValidation = new AuthVendorCallbackValidation();
        authVendorConfig.setCallbackValidation(authVendorCallbackValidation);
        when(authVendorChannelConfigRepository.getAuthVendorConfiguration(anyString())).thenReturn(authVendorConfig);
        try {
            atmCartService.registerFootballFriendDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG.getErrorCode(), e.getErrorCode());
        }

        authVendorCallbackValidation.setId(5L);
        PartnerInfoResponse partnerInfoResponse = null;
        when(intAvetDispatcherRepository.getPartnerInformation(anyLong(), anyString(), anyString(), anyInt())).thenReturn(partnerInfoResponse);
        try {
            atmCartService.registerFootballFriendDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        //Fail on partner info with wrong permission
        partnerInfoResponse = new PartnerInfoResponse();
        partnerInfoResponse.setIdPermiso(253);
        when(intAvetDispatcherRepository.getPartnerInformation(anyLong(), anyString(), anyString(), anyInt())).thenReturn(partnerInfoResponse);
        try {
            atmCartService.registerFootballFriendDiscount(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.BLOCKED_USER.getErrorCode(), e.getErrorCode());
        }

        partnerInfoResponse.setIdPermiso(254);
        atmCartService.registerFootballFriendDiscount(cartToken, atmCartRequest);
    }

    @Test
    void addTourFriendTicketTest() throws Exception {
        when(AuthContextUtils.getToken()).thenReturn("entityId");
        // atmCartRequest null
        try {
            final String cartToken = "fjlkfjgkls";
            atmCartService.addTourFriendTicket(cartToken, null);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.SHOPPING_CART_TOKEN_MANDATORY.getErrorCode());
        }

        ATMFriendCodeCartRequest atmCartRequest = new ATMFriendCodeCartRequest();
        atmCartRequest.setSessionId(4L);
        // Cart token null
        try {
            String emptyCartToken = null;
            atmCartService.addTourFriendTicket(emptyCartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.SHOPPING_CART_TOKEN_MANDATORY.getErrorCode());
        }

        doNothing().when(shoppingCartRepository).validateCart(anyString());
        AuthVendorChannelConfig authVendorChannelConfig = new AuthVendorChannelConfig();
        List<String> vendors = new ArrayList<>();
        vendors.add("234234");
        authVendorChannelConfig.setVendors(vendors);
        when(authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(anyLong())).thenReturn(authVendorChannelConfig);
        final String cartToken = "sdfsdf";
        // friend code null
        try {
            atmCartService.addTourFriendTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.FRIEND_CODE_MANDATORY.getErrorCode());
        }

        atmCartRequest.setCode("");
        try {
            atmCartService.addTourFriendTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.FRIEND_CODE_MANDATORY.getErrorCode());
        }

        atmCartRequest.setCode("ABCD");
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(null);
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setChannelId(4L);
        authenticationData.setEntityId(1L);
        when(AuthenticationUtils.getAuthDataOrNull()).thenReturn(authenticationData);

        try {
            atmCartService.addTourFriendTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        // User data additional data null
        vendors.add(ATM_VENDOR_ID);
        authVendorChannelConfig.setVendors(vendors);
        when(authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(anyLong())).thenReturn(authVendorChannelConfig);
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(null);

        try {
            atmCartService.addTourFriendTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        // User data empty additional data
        AuthVendorUserData authVendorUserData = getAuthVendorUserData();
        authVendorUserData.setAdditionalData(getEmptyAdditionalData());
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(authVendorUserData);

        try {
            atmCartService.addTourFriendTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        //Additional data no classification
        Map<String, Object> additionalData = getNoClassificationAdditionalData();
        authVendorUserData.setAdditionalData(additionalData);
        when(authVendorChannelConfigRepository.getUserData(anyString(), any())).thenReturn(authVendorUserData);
        try {
            atmCartService.addTourFriendTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_CREDENTIALS.getErrorCode(), e.getErrorCode());
        }

        additionalData.put(ATMVendorConstants.CLASSIFICATION, ATMClassification.VIP_MEMBER.name());
        try {
            atmCartService.addTourFriendTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.INVALID_USERDATA.getErrorCode(), e.getErrorCode());
        }

        SessionInfo sessionInfo = getSessionInfo();
        when(shoppingCartRepository.getSessionInfo(anyLong())).thenReturn(sessionInfo);

        AuthVendorConfig authVendorConfig = new AuthVendorConfig();
        AuthVendorCallbackValidation authVendorCallbackValidation = new AuthVendorCallbackValidation();
        authVendorCallbackValidation.setId(5L);
        authVendorConfig.setCallbackValidation(authVendorCallbackValidation);

        when(authVendorChannelConfigRepository.getAuthVendorConfiguration(anyString())).thenReturn(authVendorConfig);
        UserApplicableGroup userApplicableGroup = ObjectRandomizer.random(UserApplicableGroup.class);
        when(shoppingCartRepository.getUserGroupValidation(anyString(), anyLong(), anyString(), anyString(), anyLong())).thenReturn(userApplicableGroup);

        //Fail on partner info with wrong permission
        PartnerInfoResponse partnerInfoResponse = new PartnerInfoResponse();
        partnerInfoResponse.setIdPermiso(253);
        when(intAvetDispatcherRepository.getPartnerInformation(anyLong(), anyString(), anyString(), anyInt())).thenReturn(partnerInfoResponse);
        try {
            atmCartService.addTourFriendTicket(cartToken, atmCartRequest);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(ApiExternalErrorCode.BLOCKED_USER.getErrorCode(), e.getErrorCode());
        }

        partnerInfoResponse.setIdPermiso(254);
        atmCartService.addTourFriendTicket(cartToken, atmCartRequest);
    }


    private Map<String, Object> getEmptyAdditionalData() {
        return getAdditionalData();
    }

    private Map<String, Object> getNoClassificationAdditionalData() {
        return getAdditionalData(
                new AbstractMap.SimpleEntry<>(ATMVendorConstants.USERDATA_DISCOUNT_TYPE, CORRECT_DISCOUNT_TYPE),
                new AbstractMap.SimpleEntry<>(ATMVendorConstants.MEMBERSHIP_ID, "1234"),
                new AbstractMap.SimpleEntry<>(ATMVendorConstants.AVET_PIN, "5678"));
    }

    private Map<String, Object> getAdditionalData(Map.Entry<String, Object>... entries) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private SessionInfo getSessionInfo() {
        SessionInfo sessionInfo = new SessionInfo();
        ActivityTicketTypesAvailability activityTicketTypesAvailability = new ActivityTicketTypesAvailability();
        List<ActivityTicketTypeAvailability> activityTicketTypeAvailabilities = new ArrayList<>();
        ActivityTicketTypeAvailability activityTicketTypeAvailability = new ActivityTicketTypeAvailability();
        activityTicketTypeAvailability.setId(BigInteger.valueOf(27));
        activityTicketTypeAvailability.setCode(CORRECT_DISCOUNT_TYPE);
        activityTicketTypeAvailabilities.add(activityTicketTypeAvailability);
        activityTicketTypesAvailability.setActivityTicketTypeAvailability(activityTicketTypeAvailabilities);
        sessionInfo.setActivityTicketTypesAvailability(activityTicketTypesAvailability);
        return sessionInfo;
    }

    private AuthVendorUserData getAuthVendorUserData() {
        AuthVendorUserData authVendorUserData = new AuthVendorUserData();
        authVendorUserData.setId("X09876Y");
        authVendorUserData.setName("John");
        authVendorUserData.setSurname("Doe");
        authVendorUserData.setSecondSurname("Second");
        authVendorUserData.setEmail("jdoe@fake.com");
        return authVendorUserData;
    }

}
