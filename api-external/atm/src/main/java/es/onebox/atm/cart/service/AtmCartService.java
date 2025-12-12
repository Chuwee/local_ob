package es.onebox.atm.cart.service;

import es.onebox.atm.cart.ATMVendorConstants;
import es.onebox.atm.cart.dto.ATMFriendCodeCartRequest;
import es.onebox.atm.cart.dto.ATMMemberCartRequest;
import es.onebox.atm.cart.dto.AbstractATMCartRequest;
import es.onebox.atm.cart.enums.ATMClassification;
import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.datasources.dispatcher.dto.PartnerInfoResponse;
import es.onebox.common.datasources.dispatcher.repositoty.IntAvetDispatcherRepository;
import es.onebox.common.datasources.ms.client.dto.AuthVendorChannelConfig;
import es.onebox.common.datasources.ms.client.dto.AuthVendorConfig;
import es.onebox.common.datasources.ms.client.dto.AuthVendorUserData;
import es.onebox.common.datasources.ms.client.repository.AuthVendorChannelConfigRepository;
import es.onebox.common.datasources.rest.dto.ns.data_query.prices.ActivityTicketTypeAvailability;
import es.onebox.common.datasources.rest.dto.ns.data_query.session.SessionInfo;
import es.onebox.common.datasources.rest.repository.ShoppingCartRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
public class AtmCartService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmCartService.class);
    private final AuthVendorChannelConfigRepository authVendorChannelConfigRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final IntAvetDispatcherRepository intAvetDispatcherRepository;

    @Autowired
    public AtmCartService(AuthVendorChannelConfigRepository authVendorChannelConfigRepository,
                          ShoppingCartRepository shoppingCartRepository, IntAvetDispatcherRepository intAvetDispatcherRepository) {
        this.authVendorChannelConfigRepository = authVendorChannelConfigRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.intAvetDispatcherRepository = intAvetDispatcherRepository;
    }

    public void registerFootballMemberDiscount(final String cartToken, final ATMMemberCartRequest atmCartRequest) {
        LOGGER.info("[ATMIDENTITY] registerFootballMemberDiscount: {}", atmCartRequest);
        validateCart(atmCartRequest, cartToken);
        Long channelId = getChannelId();
        AuthVendorChannelConfig authVendorChannelConfig = validateVendorChannelConfiguration(channelId);
        AuthVendorUserData userData = getLoggedMemberUserData(atmCartRequest.getMemberId(), authVendorChannelConfig);
        validateMemberDiscountAllowed(userData);
        AuthVendorConfig authVendorConfig = getATMVendorConfig(authVendorChannelConfig);
        validatePermission(userData);
        addFootballDiscount(cartToken, authVendorConfig.getCallbackValidation().getId(),
                atmCartRequest.getSessionId(), userData);
    }

    public void addTourMemberTicket(final String cartToken, final ATMMemberCartRequest atmCartRequest) {
        LOGGER.info("[ATMIDENTITY] addTourMemberTicket: {}", atmCartRequest);
        validateCart(atmCartRequest, cartToken);
        Long channelId = getChannelId();
        AuthVendorChannelConfig authVendorChannelConfig = validateVendorChannelConfiguration(channelId);
        AuthVendorUserData userData = getLoggedMemberUserData(atmCartRequest.getMemberId(), authVendorChannelConfig);
        validateTourTicketAllowed(userData);
        validatePermission(userData);
        addTourTicket(cartToken, atmCartRequest.getSessionId(), userData);
    }

    public void registerFootballFriendDiscount(final String cartToken, final ATMFriendCodeCartRequest atmCartRequest) {
        LOGGER.info("[ATMIDENTITY] registerFootballFriendDiscount: {}", atmCartRequest);
        validateCart(atmCartRequest, cartToken);
        Long channelId = getChannelId();
        AuthVendorChannelConfig authVendorChannelConfig = validateVendorChannelConfiguration(channelId);
        AuthVendorUserData userData = getFriendCodeUserData(atmCartRequest.getCode(), authVendorChannelConfig);
        validateMemberDiscountAllowed(userData);
        AuthVendorConfig authVendorConfig = getATMVendorConfig(authVendorChannelConfig);
        validatePermission(userData);
        addFootballDiscount(cartToken, authVendorConfig.getCallbackValidation().getId(),
                atmCartRequest.getSessionId(), userData);
    }

    public void addTourFriendTicket(final String cartToken, final ATMFriendCodeCartRequest atmCartRequest) {
        LOGGER.info("[ATMIDENTITY] addTourFriendTicket: {}", atmCartRequest);
        validateCart(atmCartRequest, cartToken);
        Long channelId = getChannelId();
        AuthVendorChannelConfig authVendorChannelConfig = validateVendorChannelConfiguration(channelId);
        AuthVendorUserData userData = getFriendCodeUserData(atmCartRequest.getCode(), authVendorChannelConfig);
        validateTourTicketAllowed(userData);
        validatePermission(userData);
        addTourTicket(cartToken, atmCartRequest.getSessionId(), userData);
    }


    private AuthVendorUserData getLoggedMemberUserData(String memberId, AuthVendorChannelConfig authVendorChannelConfig) {
        Map<String, Object> vendorPayload = Collections.singletonMap(ATMVendorConstants.PARAM_MEMBER_ID, memberId);
        return getUserData(vendorPayload, authVendorChannelConfig);
    }

    private AuthVendorUserData getFriendCodeUserData(String friendCode, AuthVendorChannelConfig authVendorChannelConfig) {
        Map<String, Object> vendorPayload = new HashMap<>();
        vendorPayload.put(ATMVendorConstants.FRIEND_CODE, friendCode);
        return getUserData(vendorPayload, authVendorChannelConfig);
    }

    private AuthVendorUserData getUserData(Map<String, Object> vendorPayload, AuthVendorChannelConfig authVendorChannelConfig) {
        AuthVendorUserData userData;
        String vendorId = authVendorChannelConfig.getVendors().get(0);
        userData = authVendorChannelConfigRepository.getUserData(vendorId, vendorPayload);

        return userData;
    }

    private void addFootballDiscount(final String cartToken, final Long groupId, final Long sessionId,
                                     final AuthVendorUserData userData) {
        String user = getAdditionalData(userData, ATMVendorConstants.MEMBERSHIP_ID);
        String password = getAdditionalData(userData, ATMVendorConstants.AVET_PIN);
        try {
            shoppingCartRepository.getUserGroupValidation(cartToken, groupId, user, password, sessionId);
        } catch (Exception ex) {
            LOGGER.error("[ATMIDENTITY] Error validating member {}, error: {}", user, ex.getMessage());
            throw new OneboxRestException(ApiExternalErrorCode.ADD_PROMOTION_ERROR);
        }

    }

    private AuthVendorConfig getATMVendorConfig(AuthVendorChannelConfig authVendorChannelConfig) {
        String vendorId = authVendorChannelConfig.getVendors().get(0);
        AuthVendorConfig authVendorConfig = authVendorChannelConfigRepository.getAuthVendorConfiguration(vendorId);
        if (isNull(authVendorConfig) ||
                isNull(authVendorConfig.getCallbackValidation()) ||
                isNull(authVendorConfig.getCallbackValidation().getId())) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG);
        }
        return authVendorConfig;
    }

    private Long getChannelId() {
        AuthenticationData channelAuthData = AuthenticationUtils.getAuthDataOrNull();
        if (channelAuthData == null || channelAuthData.getChannelId() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.CHANNEL_ID_NOT_FOUND);
        }
        return channelAuthData.getChannelId();
    }

    private AuthVendorChannelConfig validateVendorChannelConfiguration(Long channelId) {
        AuthVendorChannelConfig authVendorChannelConfig = authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(channelId);
        if (authVendorChannelConfig == null || CollectionUtils.isEmpty(authVendorChannelConfig.getVendors())) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG);
        }
        return authVendorChannelConfig;
    }

    private void validateCart(AbstractATMCartRequest atmCartRequest, String cartToken) {
        if (atmCartRequest == null || cartToken == null) {
            throw new OneboxRestException(ApiExternalErrorCode.SHOPPING_CART_TOKEN_MANDATORY);
        }
        if (atmCartRequest instanceof ATMFriendCodeCartRequest
                && StringUtils.isEmpty(((ATMFriendCodeCartRequest) atmCartRequest).getCode())) {
            throw new OneboxRestException(ApiExternalErrorCode.FRIEND_CODE_MANDATORY);
        }
        if (atmCartRequest instanceof ATMMemberCartRequest
                && StringUtils.isEmpty(((ATMMemberCartRequest) atmCartRequest).getMemberId())) {
            throw new OneboxRestException(ApiExternalErrorCode.MEMBER_ID_MANDATORY);
        }

        try {
            shoppingCartRepository.validateCart(cartToken);
        } catch (Exception e) {
            throw new OneboxRestException(ApiExternalErrorCode.SHOPPING_CART_EXPIRED_OR_NOT_FOUND);
        }
    }


    private void addTourTicket(final String cartToken, final Long sessionId, final AuthVendorUserData userData) {
        String discountType = getAdditionalData(userData, ATMVendorConstants.USERDATA_DISCOUNT_TYPE);
        SessionInfo sessionInfo;
        try {
            sessionInfo = shoppingCartRepository.getSessionInfo(sessionId);
        } catch (OneboxRestException ex) {
            LOGGER.error("[ATMIDENTITY] Error obtaining sessionInfo: {}", ex.getMessage());
            throw ExceptionBuilder.build(ApiExternalErrorCode.SESSION_INFO_ERROR, ex.getErrorCode());
        }
        ActivityTicketTypeAvailability ticketType = getRelatedTicketType(sessionInfo, discountType);
        try {
            shoppingCartRepository.addIndividualActivitySeats(sessionId, ticketType.getId().longValue(), 1, cartToken);
        } catch (Exception ex) {
            LOGGER.error("[ATMIDENTITY] Error in method addTourTicketAndPromo: {}", ex.getMessage());
            shoppingCartRepository.releaseAllItems(cartToken);
            throw ExceptionBuilder.build(ApiExternalErrorCode.ERROR_REST_SHOPPINGCART_SEATS_NOT_AVAILABLE);
        }
    }

    private ActivityTicketTypeAvailability getRelatedTicketType(final SessionInfo sessionInfo,
                                                                final String discountType) {

        if (StringUtils.isEmpty(discountType)) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_MEMBER_TYPE);
        }
        if (sessionInfo == null) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.SESSION_INFO_ERROR, "session info is null");
        }

        if (sessionInfo.getActivityTicketTypesAvailability() == null ||
                CollectionUtils.isEmpty(sessionInfo.getActivityTicketTypesAvailability().getActivityTicketTypeAvailability())) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.SESSION_INFO_ERROR, "no activity ticket type availability");
        }

        return sessionInfo.getActivityTicketTypesAvailability().getActivityTicketTypeAvailability().stream()
                .filter(tp -> discountType.equals(tp.getCode()))
                .findAny()
                .orElseThrow(() -> ExceptionBuilder.build(ApiExternalErrorCode.TICKET_TYPE_ERROR, sessionInfo.getId(), discountType));
    }


    private void validateMemberDiscountAllowed(AuthVendorUserData userData) {
        ATMClassification classification = getUserClassification(userData);
        boolean allowed = switch (classification) {
            case SUBSCRIBER_MEMBER, MEMBER, VIP_MEMBER, VIP_SUBSCRIBER_MEMBER -> true;
            case FAN, RED_WHITE, VIP, SPONSOR, TOURIST_PACK, REGISTERED_USER -> false;
        };
        if (!allowed) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.INVALID_MEMBER_CLASSIFICATION, classification.name());
        }
    }

    private void validateTourTicketAllowed(AuthVendorUserData userData) {
        ATMClassification classification = getUserClassification(userData);
        boolean allowed = switch (classification) {
            case SUBSCRIBER_MEMBER, MEMBER, VIP_MEMBER, VIP_SUBSCRIBER_MEMBER, RED_WHITE -> true;
            case FAN, VIP, SPONSOR, TOURIST_PACK, REGISTERED_USER -> false;
        };
        if (!allowed) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.INVALID_MEMBER_CLASSIFICATION, classification.name());
        }
    }

    private String getAdditionalData(AuthVendorUserData userData, String key) {
        if (userData == null || MapUtils.isEmpty(userData.getAdditionalData())) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_USERDATA);
        }
        String value = (String) userData.getAdditionalData().get(key);
        if (StringUtils.isEmpty(value)) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_CREDENTIALS);
        }
        return value;
    }

    private ATMClassification getUserClassification(AuthVendorUserData userData) {
        String classificationName = getAdditionalData(userData, ATMVendorConstants.CLASSIFICATION);
        return ATMClassification.fromString(classificationName);
    }

    private void validatePermission(AuthVendorUserData userData) {
        String avetMemberId = getAdditionalData(userData, ATMVendorConstants.MEMBERSHIP_ID);
        String avetMemberPass = getAdditionalData(userData, ATMVendorConstants.AVET_PIN);

        AuthenticationData authData = AuthenticationUtils.getAuthDataOrNull();
        if (authData == null || authData.getEntityId() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ENTITY_NOT_FOUND);
        }
        Long entityId = authData.getEntityId();

        PartnerInfoResponse partnerInfoResponse = intAvetDispatcherRepository.getPartnerInformation(entityId,
                avetMemberId, avetMemberPass, ATMVendorConstants.ATM_MAIN_CAPACITY_ID);

        // Validar la respuesta del partner
        if (partnerInfoResponse == null) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_USERDATA);
        }

        if (ATMVendorConstants.ATM_ALLOWED_PERMISSION != partnerInfoResponse.getIdPermiso()) {
            throw new OneboxRestException(ApiExternalErrorCode.BLOCKED_USER);
        }

        LOGGER.info("[ATMIDENTITY] validatePermission: Partner info validated successfully for member: {}", avetMemberId);
    }
}
