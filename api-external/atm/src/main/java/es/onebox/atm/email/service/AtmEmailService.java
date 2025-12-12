package es.onebox.atm.email.service;

import es.onebox.atm.access.ATMAccessService;
import es.onebox.atm.email.config.PdfTicketGenerationService;
import es.onebox.cache.repository.CacheRepository;
import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.datasources.common.enums.OrderType;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelDeliveryMethodsDTO;
import es.onebox.common.datasources.ms.channel.dto.EmailServerDTO;
import es.onebox.common.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.common.datasources.ms.channel.enums.DeliveryMethodStatus;
import es.onebox.common.datasources.ms.channel.enums.EmailMode;
import es.onebox.common.datasources.ms.channel.enums.EmailServerType;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.event.enums.ChannelType;
import es.onebox.common.datasources.orders.dto.OrderDetail;
import es.onebox.common.datasources.orders.enums.OrderDetailsItemState;
import es.onebox.common.datasources.orders.repository.OrdersRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class AtmEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmEmailService.class);
    private final ATMAccessService accessService;
    private final OrdersRepository ordersRepository;
    private final ChannelRepository channelRepository;
    private final CacheRepository hazelcastCacheRepository;
    private final PdfTicketGenerationService pdfTicketGenerationService;

    private static final String EXTERNAL_EMAIL_PREFIX = "EXTERNAL_EMAIL";

    protected static final RetryPolicy RETRY_POLICY = new RetryPolicy().retryOn(Exception.class).withBackoff(1, 10, TimeUnit.SECONDS)
            .withMaxRetries(3);

    @Autowired
    public AtmEmailService(ATMAccessService accessService, OrdersRepository ordersRepository,
                           ChannelRepository channelRepository,
                           @Qualifier("cacheRepository") CacheRepository hazelcastCacheRepository,
                           PdfTicketGenerationService pdfTicketGenerationService) {
        this.accessService = accessService;
        this.ordersRepository = ordersRepository;
        this.channelRepository = channelRepository;
        this.hazelcastCacheRepository = hazelcastCacheRepository;
        this.pdfTicketGenerationService = pdfTicketGenerationService;
    }

    public void sendExternalEmail(String orderCode) {
        LOGGER.info("[ATM EXTERNAL EMAIL] [{}] Starting email send", orderCode);
        String accessToken = accessService.getAccessToken(AuthenticationService.getEntityId(), "[ATM EXTERNAL EMAIL]", orderCode);
        OrderDetail orderDetail;
        orderDetail = Failsafe.with(RETRY_POLICY).onRetry((c, failure, ctx)
                        -> LOGGER.warn("[{}] Find order on elastic. Failure: {}. Retrying {}",
                        orderCode, failure, ctx.getExecutions()))
                .onRetriesExceeded(ctx
                        -> LOGGER.error("[{}] Error finding the order on elastic: Max retries exceeded.", orderCode))
                .withFallback(() -> null)
                .get(() -> ordersRepository.getById(orderCode, accessToken));

        if (orderDetail == null) {
            LOGGER.error("[ATM EXTERNAL EMAIL] Order not found: {}", orderCode);
            throw new OneboxRestException(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }

        validateOrder(orderDetail);

        ChannelDTO channelDTO = channelRepository.getChannel(orderDetail.getChannel().getId());
        validateChannel(channelDTO);

        ChannelDeliveryMethodsDTO channelDeliveryMethodsDTO = channelRepository.getChannelDeliveryMethods(orderDetail.getChannel().getId());
        validateChannelDeliveryMethods(channelDeliveryMethodsDTO);

        EmailServerDTO emailServerDTO = channelRepository.getChannelEmailServerConfiguration(orderDetail.getChannel().getId());
        validateEmailServer(emailServerDTO);

        boolean sendTicket = channelDeliveryMethodsDTO.getEmailMode().equals(EmailMode.ONLY_TICKET)
                || channelDeliveryMethodsDTO.getEmailMode().equals(EmailMode.TICKET_AND_RECEIPT)
                || channelDeliveryMethodsDTO.getEmailMode().equals(EmailMode.UNIFIED_TICKET_AND_RECEIPT);
        boolean sendReceipt = channelDeliveryMethodsDTO.getEmailMode().equals(EmailMode.ONLY_RECEIPT)
                || channelDeliveryMethodsDTO.getEmailMode().equals(EmailMode.TICKET_AND_RECEIPT)
                || channelDeliveryMethodsDTO.getEmailMode().equals(EmailMode.UNIFIED_TICKET_AND_RECEIPT)
                || channelDeliveryMethodsDTO.getEmailMode().equals(EmailMode.RECEIPT_AND_PASSBOOK);

        try {
            putCache(orderCode);
            if (sendTicket && sendReceipt) {
                pdfTicketGenerationService.sendTicketAndReceiptEmail(orderCode, orderDetail.getLanguage(), false);
            } else if (sendTicket) {
                pdfTicketGenerationService.sendTicketEmail(orderCode, orderDetail.getLanguage(), false);
            } else if (sendReceipt) {
                pdfTicketGenerationService.sendReceiptEmail(orderCode, orderDetail.getLanguage());
            }
        } catch (Exception e) {
            deleteCache(orderCode);
            LOGGER.warn("[ATM EXTERNAL EMAIL] PrintAtHome Message could not be send for orderCode:" + orderCode, e);
        }

        LOGGER.info("[ATM EXTERNAL EMAIL] [{}] Email request sent", orderCode);
    }

    private void validateOrder(OrderDetail orderDetail) {
        checkCache(orderDetail.getCode());
        if (orderDetail.getItems().stream().anyMatch(ti ->
                ti.getTicket() != null
                        && ti.getTicket().getAllocation() != null
                        && ti.getTicket().getAllocation().getSession() != null
                        && ti.getTicket().getAllocation().getSession().getDate() != null
                        && ti.getTicket().getAllocation().getSession().getDate().getStart().isBefore(ChronoZonedDateTime.from(ZonedDateTime.now())))) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_ORDER_DATE);
        }
        if (orderDetail.getItems().stream().allMatch(ti ->
                ti.getState().equals(OrderDetailsItemState.REFUNDED)
                        || ti.getState().equals(OrderDetailsItemState.EXPIRED))) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_ORDER_STATE);
        }
        if (!orderDetail.getType().equals(OrderType.PURCHASE)) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_ORDER_TYPE);
        }
        if (orderDetail.getChannel() == null || orderDetail.getChannel().getId() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.CHANNEL_ID_NOT_FOUND);
        }
    }

    private void checkCache(String orderCode) {
        String key = hazelcastCacheRepository.buildKey(EXTERNAL_EMAIL_PREFIX, new String[]{orderCode});
        ZonedDateTime lastRequest = hazelcastCacheRepository.get(key, ZonedDateTime.class);
        LOGGER.info("lastRequest: {}", lastRequest);
        if (lastRequest != null) {
            throw new OneboxRestException(ApiExternalErrorCode.REQUEST_ALREADY_IN_PROCESS);
        }
    }

    private void putCache(String orderCode) {
        hazelcastCacheRepository.set(EXTERNAL_EMAIL_PREFIX, ZonedDateTime.now(), 2, TimeUnit.MINUTES, new Object[]{orderCode});
    }

    private void deleteCache(String orderCode) {
        hazelcastCacheRepository.remove(EXTERNAL_EMAIL_PREFIX, new String[]{orderCode});
    }

    private void validateChannel(ChannelDTO channelDTO) {
        if (channelDTO == null) {
            throw new OneboxRestException(ApiExternalErrorCode.CHANNEL_NOT_FOUND);
        }
        if (channelDTO.getEntityId() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.CHANNEL_ENTITY_NOT_FOUND);
        }
        if (channelDTO.getStatus() == null || !channelDTO.getStatus().equals(ChannelStatus.ACTIVE)) {
            throw new OneboxRestException(ApiExternalErrorCode.WRONG_CHANNEL_STATUS);
        }
        if (channelDTO.getType() == null || !channelDTO.getType().equals(ChannelType.OB_PORTAL)) {
            throw new OneboxRestException(ApiExternalErrorCode.WRONG_CHANNEL_TYPE);
        }
    }

    private void validateChannelDeliveryMethods(ChannelDeliveryMethodsDTO channelDeliveryMethodsDTO) {
        if (channelDeliveryMethodsDTO == null) {
            throw new OneboxRestException(ApiExternalErrorCode.CHANNEL_DELIVERY_METHODS_NOT_FOUND);
        }
        if (channelDeliveryMethodsDTO.getEmailMode() == null || channelDeliveryMethodsDTO.getEmailMode().equals(EmailMode.NONE)) {
            throw new OneboxRestException(ApiExternalErrorCode.CHANNEL_DELIVERY_METHODS_NOT_FOUND);
        }
        if (channelDeliveryMethodsDTO.getDeliveryMethods().stream().noneMatch(dm -> dm.getStatus().equals(DeliveryMethodStatus.ACTIVE))) {
            throw new OneboxRestException(ApiExternalErrorCode.CHANNEL_DELIVERY_METHODS_NOT_FOUND);
        }
    }

    private void validateEmailServer(EmailServerDTO emailServerDTO) {
        if (emailServerDTO == null || emailServerDTO.getType() == null || emailServerDTO.getType().equals(EmailServerType.ONEBOX)) {
            throw new OneboxRestException(ApiExternalErrorCode.WRONG_SERVER_TYPE);
        }
        if (emailServerDTO.getConfiguration() == null || emailServerDTO.getConfiguration().getServer() == null
                || emailServerDTO.getConfiguration().getPort() == null || emailServerDTO.getConfiguration().getSecurity() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.EMAIL_SERVER_CONFIG_INCOMPLETE);
        }
    }
}
