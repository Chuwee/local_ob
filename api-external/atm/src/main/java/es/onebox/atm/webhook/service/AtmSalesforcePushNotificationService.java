package es.onebox.atm.webhook.service;

import es.onebox.atm.access.ATMAccessService;
import es.onebox.atm.cart.ATMVendorConstants;
import es.onebox.atm.webhook.converter.WebhookMessageConverter;
import es.onebox.common.datasources.ms.client.dto.AuthVendorConfig;
import es.onebox.common.datasources.ms.client.repository.AuthVendorChannelConfigRepository;
import es.onebox.common.datasources.ms.entity.dto.CountrySubdivisionDTO;
import es.onebox.common.datasources.ms.entity.repository.MasterDataRepository;
import es.onebox.common.datasources.orders.repository.OrdersRepository;
import es.onebox.common.datasources.webhook.WebhookDatasource;
import es.onebox.common.datasources.webhook.dto.OrderNotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.atm.OAuth2TokenResponse;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AtmSalesforcePushNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmSalesforcePushNotificationService.class);

    private final ATMAccessService accessService;
    private final MasterDataRepository masterDataRepository;
    private final OrdersRepository ordersRepository;
    private final AuthVendorChannelConfigRepository authVendorChannelConfigRepository;
    private final WebhookDatasource webhookDatasource;

    @Autowired
    public AtmSalesforcePushNotificationService(ATMAccessService accessService, OrdersRepository ordersRepository,
                                                MasterDataRepository masterDataRepository,
                                                AuthVendorChannelConfigRepository authVendorChannelConfigRepository,
                                                WebhookDatasource webhookDatasource) {
        this.accessService = accessService;
        this.ordersRepository = ordersRepository;
        this.masterDataRepository = masterDataRepository;
        this.authVendorChannelConfigRepository = authVendorChannelConfigRepository;
        this.webhookDatasource = webhookDatasource;
    }


    public void pushOrderToSalesforce(String orderCode, String orderType, String apiKey, Long entityId,
                                      Map<String, String> headers) {
        AuthVendorConfig authVendorConfig = authVendorChannelConfigRepository.getAuthVendorConfiguration("ATMIDENTITY");
        if (authVendorConfig == null || authVendorConfig.getProperties() == null) {
            LOGGER.error("[ATM SALESFORCE NOTIFICATION] [{}] auth vendor config not found", orderCode);
            throw ExceptionBuilder.build(ApiExternalErrorCode.AUTHVENDOR_CONFIG_NOT_FOUND);
        }
        OAuth2TokenResponse atmAuthResponse;
        try {
            atmAuthResponse = webhookDatasource.getATMAccessToken(authVendorConfig.getProperties().get("auth_code_url"), authVendorConfig.getProperties().get("client_id"),
                    authVendorConfig.getProperties().get("client_secret"), authVendorConfig.getProperties().get("username"), authVendorConfig.getProperties().get("password"));
        } catch (Exception e) {
            LOGGER.error("[ATM SALESFORCE NOTIFICATION] [{}] error obtaining salesforce auth token", orderCode, e);
            throw e;
        }

        try {
            OrderNotificationMessageDTO messageDTO = getATMPushNotificationMessage(orderCode, orderType, apiKey, entityId, headers);
            webhookDatasource.sendATMNotification(atmAuthResponse, messageDTO, orderCode);
        } catch (Exception e) {
            LOGGER.error("[ATM SALESFORCE NOTIFICATION] [{}] error pushing order to salesforce", orderCode, e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
        LOGGER.info("[ATM SALESFORCE NOTIFICATION] [{}] Notification of type {} sent", orderCode, orderType);
    }

    public OrderNotificationMessageDTO getATMPushNotificationMessage(String orderCode, String orderType, String apiKey,
                                                                     Long entityId, Map<String, String> headers) throws Exception {
        String accessToken = accessService.getAccessToken(entityId, "[ATM SALESFORCE NOTIFICATION]", orderCode);
        HashMap rawOrder = null;
        try {
            if (ATMVendorConstants.TICKETING_ORDER_TYPE.equals(orderType)) {
                rawOrder = ordersRepository.getRawOrder(orderCode, accessToken);
            } else if (ATMVendorConstants.MEMBER_ORDER_TYPE.equals(orderType)) {
                rawOrder = ordersRepository.getRawMemberOrder(orderCode, accessToken);
            }
        } catch (Exception e) {
            LOGGER.error("[ATM SALESFORCE NOTIFICATION] [{}] error obtaining order", orderCode, e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
        //This is needed to get the state
        Map<String, CountrySubdivisionDTO> subdivisionDTOMap = masterDataRepository.getSubdivisionsByCountryCode();

        return WebhookMessageConverter.convert(rawOrder, apiKey, subdivisionDTOMap, headers);
    }
}
