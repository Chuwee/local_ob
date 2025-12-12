package es.onebox.atm.users.service;

import es.onebox.atm.cart.ATMVendorConstants;
import es.onebox.atm.cart.enums.ATMClassification;
import es.onebox.atm.users.converter.UserPromotionConverter;
import es.onebox.atm.users.dto.ATMAddPromotionRequest;
import es.onebox.atm.users.dto.ATMOauthResponse;
import es.onebox.atm.users.dto.ATMUserPromotion;
import es.onebox.atm.users.dto.ATMUserPromotionDTO;
import es.onebox.atm.users.dto.DiscountType;
import es.onebox.common.datasources.distribution.dto.PresalesRequest;
import es.onebox.atm.users.repository.ATMPromotionsRepository;
import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.datasources.distribution.dto.OrderResponse;
import es.onebox.common.datasources.distribution.repository.DistributionRepository;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.client.dto.AuthVendorChannelConfig;
import es.onebox.common.datasources.ms.client.dto.AuthVendorConfig;
import es.onebox.common.datasources.ms.client.dto.AuthVendorUserData;
import es.onebox.common.datasources.ms.client.repository.AuthVendorChannelConfigRepository;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class AtmUsersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmUsersService.class);

    private final AuthVendorChannelConfigRepository authVendorChannelConfigRepository;
    private final ATMPromotionsRepository atmPromotionsRepository;
    private final DistributionRepository distributionRepository;

    private static final String ATM_API_OAUTH_URL = "atm_api_oauth_url";
    private static final String ATM_API_CLIENT_ID = "atm_api_client_id";
    private static final String ATM_API_CLIENT_SECRET = "atm_api_client_secret";
    private static final String ATM_API_DOMAIN = "atm_api_domain";
    private static final String PROMO_API_PATH = "promotions_api_path";
    private static final String PROMO_API_PATH_PARAMS = "promotions_api_path_params";
    public static final String MEMBERSHIP_ID = "membership_id";
    public static final String AVET_PIN = "avet_pin";
    public static final String CLASSIFICATION = "classification";
    private static final String FIRST_FIELD = "FIELD_1";
    private static final String SECOND_FIELD = "FIELD_2";

    @Autowired
    public AtmUsersService(AuthVendorChannelConfigRepository authVendorChannelConfigRepository,
                           ATMPromotionsRepository atmPromotionsRepository,
                           DistributionRepository distributionRepository) {
        this.authVendorChannelConfigRepository = authVendorChannelConfigRepository;
        this.atmPromotionsRepository = atmPromotionsRepository;
        this.distributionRepository = distributionRepository;
    }

    public List<AuthVendorUserData> getRelatedUsers(final String tutorId) {
        LOGGER.info("[ATMIDENTITY] getRelatedUsers: tutorId: {}", tutorId);
        Long channelId = getChannelId();
        AuthVendorChannelConfig authVendorChannelConfig = validateVendorChannelConfiguration(channelId);
        return getRelatedUsers(tutorId, authVendorChannelConfig);
    }

    public List<ATMUserPromotionDTO> getUserPromotions(final String userSalesforceId) {
        LOGGER.info("[ATM PROMOTION] getUserPromotions: userSalesforceId: {}", userSalesforceId);
        Long channelId = getChannelId();
        AuthVendorChannelConfig authVendorChannelConfig = validateVendorChannelConfiguration(channelId);
        String vendorId = authVendorChannelConfig.getVendors().get(0);
        AuthVendorConfig authVendorConfig = authVendorChannelConfigRepository.getAuthVendorConfiguration(vendorId);
        if (isNull(authVendorConfig) ||
                isNull(authVendorConfig.getCallbackValidation()) ||
                isNull(authVendorConfig.getCallbackValidation().getId())) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG);
        }

        String atmApiToken = login(authVendorConfig);
        List<ATMUserPromotion> userPromotions;
        try {
            userPromotions = getUserPromotions(authVendorConfig, userSalesforceId, atmApiToken);
        } catch (Exception e) {
            LOGGER.error("[ATM PROMOTION] gertUserPromotions: Error on ATM API request with user id: {}, error: {}", userSalesforceId, e.getMessage());
            return new ArrayList<>();
        }
        List<ATMUserPromotionDTO> userPromotionDTOS = UserPromotionConverter.convert(userPromotions);
        return userPromotionDTOS.stream().filter(up -> up.getDiscountType().equals(DiscountType.FIXED)).collect(Collectors.toList());
    }

    private List<ATMUserPromotion> getUserPromotions(AuthVendorConfig authVendorConfig, String userSalesforceId, String atmApiToken) {
        if (authVendorConfig.getProperties() == null || !authVendorConfig.getProperties().containsKey(PROMO_API_PATH)
                || authVendorConfig.getProperties().get(PROMO_API_PATH) == null ||
                !authVendorConfig.getProperties().containsKey(ATM_API_DOMAIN) ||
                authVendorConfig.getProperties().get(ATM_API_DOMAIN) == null) {
            LOGGER.error("[ATM PROMOTION] There is no valid configuration on ATM promotions API url.");
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG);
        }
        String promotionsUrl = authVendorConfig.getProperties().get(ATM_API_DOMAIN) +
                authVendorConfig.getProperties().get(PROMO_API_PATH);
        String pathParams = authVendorConfig.getProperties().get(PROMO_API_PATH_PARAMS);

        return atmPromotionsRepository.getUserPromotions(promotionsUrl, userSalesforceId, atmApiToken, pathParams);
    }

    private String login(AuthVendorConfig authVendorConfig) {
        if (authVendorConfig.getProperties() == null || !authVendorConfig.getProperties().containsKey(ATM_API_OAUTH_URL)
                || !authVendorConfig.getProperties().containsKey(ATM_API_CLIENT_ID) || !authVendorConfig.getProperties().containsKey(ATM_API_CLIENT_SECRET)
                || authVendorConfig.getProperties().get(ATM_API_OAUTH_URL) == null
                || authVendorConfig.getProperties().get(ATM_API_CLIENT_ID) == null || authVendorConfig.getProperties().get(ATM_API_CLIENT_SECRET) == null) {
            LOGGER.error("[ATM PROMOTION] There is no valid configuration on ATM promotions authentication method.");
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG);
        }

        String loginUrl = authVendorConfig.getProperties().get(ATM_API_OAUTH_URL);
        String clientId = authVendorConfig.getProperties().get(ATM_API_CLIENT_ID);
        String secret = authVendorConfig.getProperties().get(ATM_API_CLIENT_SECRET);

        ATMOauthResponse atmOauthResponse;
        try {
            atmOauthResponse = atmPromotionsRepository.login(loginUrl, clientId, secret);
        } catch (Exception e) {
            LOGGER.error("[ATM PROMOTION] Error on ATM login with client: {} and secret: {}. Error: {}", clientId, secret, e.getMessage());
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG);
        }
        if (atmOauthResponse == null || atmOauthResponse.getToken() == null) {
            LOGGER.error("[ATM PROMOTION] Error on ATM login with client: {} and secret: {}. Token null", clientId, secret);
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG);
        }
        return atmOauthResponse.getToken();
    }

    public void addPromotion(final String userSalesforceId, final String promotionCode,
                             final ATMAddPromotionRequest atmAddPromotionRequest, String sessionPreviewToken) {
        LOGGER.info("[ATM PROMOTION] addPromotion: userSalesforceId: {}, promotionCode: {}", userSalesforceId, promotionCode);
        Long channelId = getChannelId();
        AuthVendorChannelConfig authVendorChannelConfig = validateVendorChannelConfiguration(channelId);
        String vendorId = authVendorChannelConfig.getVendors().get(0);
        String channelOauthToken = AuthContextUtils.getToken();

        AuthVendorConfig authVendorConfig = authVendorChannelConfigRepository.getAuthVendorConfiguration(vendorId);
        if (isNull(authVendorConfig) ||
                isNull(authVendorConfig.getCallbackValidation()) ||
                isNull(authVendorConfig.getCallbackValidation().getId())) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG);
        }
        String atmApiToken = login(authVendorConfig);

        List<ATMUserPromotion> userPromotions = getUserPromotions(authVendorConfig, userSalesforceId, atmApiToken);

        if (userPromotions == null || userPromotions.isEmpty()) {
            LOGGER.error("[ATM PROMOTION] There arent promotions on ATM API request, for user {}", userSalesforceId);
            throw new OneboxRestException(ApiExternalErrorCode.PROMOTION_NOT_FOUND);
        }
        Optional<ATMUserPromotion> atmUserPromotionDTOOpt = userPromotions.stream().filter(up -> up.getPromotionId().equals(promotionCode)).findFirst();
        if (atmUserPromotionDTOOpt.isEmpty()) {
            LOGGER.error("[ATM PROMOTION] Promotion {} not found on ATM API request, for user {}", promotionCode, userSalesforceId);
            throw new OneboxRestException(ApiExternalErrorCode.PROMOTION_NOT_FOUND);
        }

        ATMUserPromotionDTO atmUserPromotionDTO = UserPromotionConverter.convert(atmUserPromotionDTOOpt.get());
        String discountType = atmUserPromotionDTO.getDiscountType().toString();
        Double discountValue = atmUserPromotionDTO.getValue();

        // add promotion
        distributionRepository.addCartPromotion(channelOauthToken, atmAddPromotionRequest.getCartToken(), atmAddPromotionRequest.getPromotionId(),
                userSalesforceId, promotionCode, discountType, discountValue, sessionPreviewToken);
    }

    public List<AuthVendorUserData> getRelatedUsers(String tutorId, AuthVendorChannelConfig authVendorChannelConfig) {
        Map<String, Object> vendorPayload = new HashMap<>();
        vendorPayload.put(ATMVendorConstants.TUTOR_ID, tutorId);
        return getRelatedUsers(vendorPayload, authVendorChannelConfig);
    }

    public List<AuthVendorUserData> getRelatedUsers(Map<String, Object> vendorPayload, AuthVendorChannelConfig authVendorChannelConfig) {
        String vendorId = authVendorChannelConfig.getVendors().get(0);
        return authVendorChannelConfigRepository.getRelatdUsers(vendorId, vendorPayload);

    }

    public OrderResponse validateMemberPresale(final String userSalesforceId, String orderId, Long sessionId) {
        LOGGER.info("[ATMIDENTITY] getExternalUserData: userSalesforceId: {}", userSalesforceId);

        Long channelId = getChannelId();
        AuthVendorChannelConfig authVendorChannelConfig = validateVendorChannelConfiguration(channelId);

        String channelOauthToken = AuthContextUtils.getToken();

        AuthVendorUserData authVendorUserData = getUserData(userSalesforceId, authVendorChannelConfig);

        validateMemberPresaleAllowed(authVendorUserData);

        return distributionRepository.validatePresales(channelOauthToken, buildMemberPresale(authVendorUserData, sessionId), orderId);
    }

    private AuthVendorUserData getUserData(String userSalesforceId, AuthVendorChannelConfig authVendorChannelConfig) {
        Map<String, Object> vendorPayload = new HashMap<>();
        vendorPayload.put(ATMVendorConstants.PARAM_MEMBER_ID, userSalesforceId);
        String vendorId = authVendorChannelConfig.getVendors().get(0);
        return authVendorChannelConfigRepository.getUserData(vendorId, vendorPayload);
    }

    private PresalesRequest buildMemberPresale(AuthVendorUserData userData, Long sessionId) {
        String avetId = getUserField(userData, MEMBERSHIP_ID);
        String avetPin = getUserField(userData, AVET_PIN);

        Map<String, String> userMap = new HashMap<>();
        userMap.put(FIRST_FIELD, avetId);
        userMap.put(SECOND_FIELD, avetPin);

        PresalesRequest presale = new PresalesRequest();
        presale.setSessionId(sessionId);
        presale.setFields(userMap);

        return presale;
    }

    private void validateMemberPresaleAllowed(AuthVendorUserData userData) {
        String atmClassification = getUserField(userData, CLASSIFICATION);

        if (atmClassification == null) {
            throw new OneboxRestException(ApiExternalErrorCode.CLASSIFICATION_NOT_FOUND);
        }

        ATMClassification classification = ATMClassification.fromString(atmClassification);
        boolean allowed = switch (classification) {
            case SUBSCRIBER_MEMBER, MEMBER, VIP_MEMBER, VIP_SUBSCRIBER_MEMBER -> true;
            case FAN, RED_WHITE, VIP, SPONSOR, TOURIST_PACK, REGISTERED_USER -> false;
        };
        if (!allowed) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_MEMBER_CLASSIFICATION);
        }
    }

    private Long getChannelId() {
        AuthenticationData channelAuthData = AuthenticationUtils.getAuthDataOrNull();
        if (channelAuthData == null || channelAuthData.getChannelId() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.CHANNEL_ID_NOT_FOUND);
        }
        return AuthenticationUtils.getAuthDataOrNull().getChannelId();
    }

    private AuthVendorChannelConfig validateVendorChannelConfiguration(Long channelId) {
        AuthVendorChannelConfig authVendorChannelConfig = authVendorChannelConfigRepository.getAuthVendorChannelConfiguration(channelId);
        if (authVendorChannelConfig == null || CollectionUtils.isEmpty(authVendorChannelConfig.getVendors())) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG);
        }
        return authVendorChannelConfig;
    }

    public String getUserField(AuthVendorUserData user, String fieldKey) {
        if (user.getAdditionalData() != null && user.getAdditionalData().containsKey(fieldKey)) {
            return (String) user.getAdditionalData().get(fieldKey);
        }
        throw new OneboxRestException(ApiExternalErrorCode.USER_NOT_FOUND);
    }
}
