package es.onebox.atm.webhook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.atm.barcode.eip.ATMExternalBarcodeMessage;
import es.onebox.atm.cart.ATMVendorConstants;
import es.onebox.atm.wallet.eip.ATMExternalWalletMessage;
import es.onebox.atm.webhook.dto.PayloadRequestDTO;
import es.onebox.atm.webhook.eip.ATMSalesforcePushNotificationMessage;
import es.onebox.common.datasources.ms.notification.dto.NotificationConfigDTO;
import es.onebox.common.datasources.ms.notification.repository.MsNotificationRepository;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.webhook.dto.OrderNotificationMessageDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.GeneratorUtils;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class AtmWebhookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmWebhookService.class);

    private final MsNotificationRepository msNotificationRepository;
    private final MsOrderRepository msOrderRepository;
    private final DefaultProducer atmExternalBarcodeProducer;
    private final DefaultProducer atmExternalWalletProducer;
    private final DefaultProducer atmSalesforcePushNotificationProducer;
    private final ObjectMapper objectMapper;
    private AtmSalesforcePushNotificationService atmSalesforcePushNotificationService;


    @Autowired
    public AtmWebhookService(MsNotificationRepository msNotificationRepository,
                             MsOrderRepository msOrderRepository,
                             @Qualifier("atmExternalBarcodeProducer") DefaultProducer atmExternalBarcodeProducer,
                             @Qualifier("atmExternalWalletProducer") DefaultProducer atmExternalWalletProducer,
                             @Qualifier("atmSalesforcePushNotificationProducer") DefaultProducer atmSalesforcePushNotificationProducer,
                             ObjectMapper objectMapper, AtmSalesforcePushNotificationService atmSalesforcePushNotificationService) {
        this.msNotificationRepository = msNotificationRepository;
        this.msOrderRepository = msOrderRepository;
        this.atmExternalBarcodeProducer = atmExternalBarcodeProducer;
        this.atmExternalWalletProducer = atmExternalWalletProducer;
        this.atmSalesforcePushNotificationProducer = atmSalesforcePushNotificationProducer;
        this.objectMapper = objectMapper;
        this.atmSalesforcePushNotificationService = atmSalesforcePushNotificationService;
    }

    public void webhookNotification(HttpServletRequest request) {
        String bodyContent;
        try {
            bodyContent = IOUtils.toString(request.getReader());
        } catch (IOException e) {
            LOGGER.error("[ATM WEBHOOK] error while reading request body", e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.GENERIC_ERROR);
        }
        PayloadRequestDTO body;
        try {
            body = objectMapper.readValue(bodyContent, PayloadRequestDTO.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("[ATM WEBHOOK] error while parsing body", e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.GENERIC_ERROR);
        }
        String orderType = request.getHeader("ob-event");
        String orderCode = body.getOrderCode();
        LOGGER.info("[ATM WEBHOOK] [{}] start processing order of type {}", orderCode, orderType);

        NotificationConfigDTO config = msNotificationRepository.getNotificationConfig(request.getHeader("ob-hook-id"));
        if (config == null) {
            LOGGER.error("[ATM WEBHOOK] [{}] webhook config not found", orderCode);
            throw ExceptionBuilder.build(ApiExternalErrorCode.WEBHOOK_CONFIG_NOT_FOUND);
        }
        String headerSignature = request.getHeader("ob-signature");

        validatePayload(headerSignature, bodyContent, config.getApiKey(), orderCode);

        OrderDTO order = msOrderRepository.getOrderByCode(orderCode);

        enqueueItemsForExternalBarcodes(order);

        enqueueItemsForExternalWallet(order);

        enqueuePushOrderToSalesforce(request, config, orderCode);
    }

    public OrderNotificationMessageDTO getATMWebhookMessage(String orderCode, String orderType) throws Exception{
        return atmSalesforcePushNotificationService.getATMPushNotificationMessage(orderCode, orderType, null, ATMVendorConstants.ATM_ENTITY_ID, null);
    }

    private void validatePayload(String headerSignature, String bodyContent, String apiKey, String orderCode) {
        String signature = GeneratorUtils.getHashSHA256(bodyContent + apiKey);
        if (headerSignature == null || !headerSignature.equals(signature)) {
            LOGGER.error("[ATM WEBHOOK] [{}] invalid payload for order, signature does not match", orderCode);
            throw ExceptionBuilder.build(ApiExternalErrorCode.INVALID_WEBHOOK_SIGNATURE);
        }
    }

    private void enqueueItemsForExternalBarcodes(OrderDTO order) {

        if (order == null) {
            return;
        }
        for (OrderProductDTO item : order.getProducts()) {
            if (EventType.AVET.equals(item.getEventType())) {
                ATMExternalBarcodeMessage message = new ATMExternalBarcodeMessage();
                message.setOrderCode(order.getCode());
                message.setItemId(item.getId());
                message.setEventId(item.getEventId().longValue());
                message.setSessionId(item.getSessionId().longValue());
                message.setRow(item.getTicketData().getRowName());
                message.setSeat(item.getTicketData().getNumSeat());
                message.setSectorName(item.getTicketData().getSectorName());
                message.setAttendantData(item.getAttendant().getFields());
                try {
                    atmExternalBarcodeProducer.sendMessage(message);
                } catch (Exception e) {
                    LOGGER.error("[ATM WEBHOOK] [{}] could not enqueue message for external barcode for item: {}",
                            order.getCode(), item.getId());
                }
            }
        }
    }

    private void enqueueItemsForExternalWallet(OrderDTO order) {

        if (order == null) {
            return;
        }
        for (OrderProductDTO item : order.getProducts()) {
            if (EventType.AVET.equals(item.getEventType())) {
                ATMExternalWalletMessage message = new ATMExternalWalletMessage();
                message.setOrderCode(order.getCode());
                message.setItemId(item.getId());
                message.setEventId(item.getEventId().longValue());
                message.setSessionId(item.getSessionId().longValue());
                try {
                    atmExternalWalletProducer.sendMessage(message);
                } catch (Exception e) {
                    LOGGER.error("[ATM WEBHOOK] [{}] could not enqueue message for external wallet for item: {}",
                            order.getCode(), item.getId());
                }
            }
        }
    }

    private void enqueuePushOrderToSalesforce(HttpServletRequest request, NotificationConfigDTO config, String orderCode) {
        ATMSalesforcePushNotificationMessage message = new ATMSalesforcePushNotificationMessage();
        message.setOrderCode(orderCode);
        message.setOrderType(request.getHeader("ob-event"));
        message.setApiKey(config.getApiKey());
        message.setEntityId(config.getEntityId());
        message.setHeaders(generateHeaders(request));
        try {
            atmSalesforcePushNotificationProducer.sendMessage(message);
        } catch (Exception e) {
            LOGGER.error("[ATM WEBHOOK] [{}] could not enqueue message for salesforce push notification", orderCode);
        }
    }

    private Map<String, String> generateHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        headers.put("ob-action", request.getHeader("ob-action"));
        headers.put("ob-delivery-id", request.getHeader("ob-delivery-id"));
        headers.put("ob-event", request.getHeader("ob-event"));
        return headers;
    }
}
