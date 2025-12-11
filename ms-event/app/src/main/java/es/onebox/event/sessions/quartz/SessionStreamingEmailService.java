package es.onebox.event.sessions.quartz;

import es.onebox.dal.dto.couch.enums.OrderState;
import es.onebox.dal.dto.couch.enums.OrderType;
import es.onebox.dal.dto.couch.order.OrderDTO;
import es.onebox.dal.dto.couch.order.OrderProductDTO;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.order.dto.SearchOperationsRequest;
import es.onebox.event.datasources.ms.order.dto.SearchOperationsResponse;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.events.amqp.requestchannelnotification.HandlebarComposer;
import es.onebox.event.events.amqp.sendemail.EmailType;
import es.onebox.event.events.amqp.sendemail.SendEmailService;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static es.onebox.core.utils.common.CommonUtils.isNull;

@Component
public class SessionStreamingEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionStreamingEmailService.class);

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    @Qualifier("sessionStreamingNotification")
    private HandlebarComposer sessionStreamingNotification;

    @Value("${onebox.s3.ms-event.hostname}")
    private String domain;
    @Value("${onebox.s3.ms-event.mail.session-streaming-notification.path}")
    private String fileBasePath;
    @Value("${onebox.s3.ms-event.mail.liveEndpoint}")
    private String liveEndpoint;

    public void sendEmails(Long sessionId, String orderCode) {
        if (orderCode == null) {
            sendSessionEmails(sessionId);
        } else {
            sendOrderEmail(sessionId, orderCode);
        }
    }

    private void sendSessionEmails(Long sessionId) {
        LOGGER.info("[SESSION STREAMING] id: {} - process cron to send users notification email", sessionId);

        SearchOperationsRequest request = new SearchOperationsRequest();
        request.setSessionIds(Collections.singletonList(sessionId.intValue()));
        request.setOrderTypes(Collections.singletonList(OrderType.PURCHASE));
        request.setOrderStates(Collections.singletonList(OrderState.PAID));
        request.setOperationRefunded(false);
        SearchOperationsResponse ordersResponse = ordersRepository.searchOperations(request);

        int sentEmails = 0;
        Long operations = ordersResponse.getMetadata().getTotal();
        if (operations > 0) {
            SessionStreamingEmailDataService sessionDataService = ctx.getBean(SessionStreamingEmailDataService.class);

            for (OrderDTO order : ordersResponse.getData()) {
                sentEmails += sendOrderSessionEmails(order, sessionId, sessionDataService);
            }
        }

        LOGGER.info("[SESSION STREAMING] id: {} - emails sent: {} in {} operations", sessionId, sentEmails, operations);
    }

    private void sendOrderEmail(Long sessionId, String orderCode) {
        LOGGER.info("[SESSION STREAMING] id: {} - process request to send user notification email for order {}", sessionId, orderCode);

        SearchOperationsRequest request = new SearchOperationsRequest();
        request.setCodes(Collections.singletonList(orderCode));
        request.setOrderTypes(Collections.singletonList(OrderType.PURCHASE));
        request.setOrderStates(Collections.singletonList(OrderState.PAID));
        request.setOperationRefunded(false);
        SearchOperationsResponse ordersResponse = ordersRepository.searchOperations(request);

        if (ordersResponse.getMetadata().getTotal() == 1L) {
            SessionStreamingEmailDataService sessionDataService = ctx.getBean(SessionStreamingEmailDataService.class);

            sendOrderSessionEmails(ordersResponse.getData().get(0), sessionId, sessionDataService);

            LOGGER.info("[SESSION STREAMING] id: {} - last-minute email sent to order with code: {}", sessionId, orderCode);
        } else {
            LOGGER.warn("[SESSION STREAMING] id: {} - order with code: {} not found", sessionId, orderCode);
        }
    }

    private int sendOrderSessionEmails(OrderDTO order, Long sessionId, SessionStreamingEmailDataService sessionDataService) {
        int sentEmails = 0;
        List<OrderProductDTO> sessionProducts = order.getProducts().stream().
                filter(product -> validProductForEmail(sessionId, product)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(sessionProducts)) {
            SessionConfig sessionConfig = sessionDataService.getSessionConfigCouch(sessionId);
            boolean sent = sendEmail(order, sessionConfig, sessionProducts, sessionDataService);
            sentEmails = sent ? sentEmails + sessionProducts.size() : sentEmails;
        }
        return sentEmails;
    }

    private boolean validProductForEmail(Long sessionId, OrderProductDTO product) {
        return product.getSessionId().equals(sessionId.intValue()) &&
                product.getRelatedProductState() == null;
    }

    private boolean sendEmail(OrderDTO order, SessionConfig sessionConfig, List<OrderProductDTO> sessionProducts,
                              SessionStreamingEmailDataService sessionDataService) {

        String userEmail = order.getCustomer().getEmail();

        Integer minutesToSession = 0;
        if (sessionConfig.getStreamingVendorConfig() != null) {
            if (sessionConfig.getStreamingVendorConfig().getSendEmail() != null &&
                    BooleanUtils.isFalse(sessionConfig.getStreamingVendorConfig().getSendEmail())) {
                LOGGER.info("[SESSION STREAMING] Skip sending email to " + userEmail + " - disabled on session config");
                return false;
            }
            if (sessionConfig.getStreamingVendorConfig().getEmailMinutesBeforeStart() != null) {
                minutesToSession = sessionConfig.getStreamingVendorConfig().getEmailMinutesBeforeStart();
            }
        }

        OrderProductDTO sampleProduct = sessionProducts.get(0);
        String language = order.getOrderData().getLanguage();
        Integer eventId = sampleProduct.getEventId();
        String eventName = sessionDataService.getEventName(language, eventId.longValue(),
                sampleProduct.getAdditionalData().getEventName());
        Integer sessionId = sampleProduct.getSessionId();
        String sessionName = sessionDataService.getSessionName(language, sessionId.longValue());
        String sessionVenueTZ = sessionDataService.getSessionVenueTZ(sessionId.longValue());
        ZonedDateTime sessionDate = sampleProduct.getAdditionalData().getSessionDate();
        Integer channelId = order.getOrderData().getChannelId();

        Integer year = Calendar.getInstance().get(Calendar.YEAR);

        Locale locale = isNull(language) ? new Locale("es", "ES") : LocaleUtils.toLocale(language);

        Map<String, Object> propertiesParams = new HashMap<>();
        propertiesParams.put(SessionStreamingConstants.EVENT_NAME, eventName);
        propertiesParams.put(SessionStreamingConstants.TIME, minutesToSession);

        propertiesParams.put(SessionStreamingConstants.INTRO_IMAGE_URL, buildImageUrl("tick.png"));
        propertiesParams.put(SessionStreamingConstants.IMAGE_URL, sessionDataService.getEventImageUrl(
                language, eventId.longValue(), sessionId.longValue()));

        CpanelCanalRecord channel = sessionDataService.getChannel(channelId);
        EntityDTO entity = sessionDataService.getEntity(sampleProduct.getEventEntityId());

        propertiesParams.put(SessionStreamingConstants.ENTITY_NAME, order.getAdditionalData().getChannelEntityName());
        propertiesParams.put(SessionStreamingConstants.ENTITY_CORPORATE_COLOR, entity.getCorporateColor());
        propertiesParams.put(SessionStreamingConstants.CURRENT_YEAR, year.toString());
        List<UrlLive> urlLives = new ArrayList<>();
        int index = 1;
        for (OrderProductDTO productDTO : sessionProducts) {
            UrlLive urlLive = new UrlLive();
            urlLive.setUrl(String.format(liveEndpoint, channel.getUrlintegracion(), language, productDTO.getTicketData().getBarcode()));
            urlLive.setIndex(index++);
            urlLives.add(urlLive);
        }
        propertiesParams.put(SessionStreamingConstants.TICKET_LIST, urlLives);
        propertiesParams.put(SessionStreamingConstants.UNIQUE, sessionProducts.size() == 1);
        propertiesParams.put(SessionStreamingConstants.EVENT_TITLE, eventName);
        propertiesParams.put(SessionStreamingConstants.SESSION_TITLE, sessionName);

        ZonedDateTime dateFromTimezone = ZonedDateTime.ofInstant(sessionDate.toInstant(), ZoneId.of("UTC"));
        ZonedDateTime dateToTimezone = dateFromTimezone.withZoneSameInstant(ZoneId.of(sessionVenueTZ));
        DateTimeFormatter pattern = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withLocale(locale);
        propertiesParams.put(SessionStreamingConstants.SESSION_STARTING_TIME, dateToTimezone.format(pattern));

        try {
            String subject = sessionStreamingNotification.getPropertiesMessage(locale, "SUBJECT", propertiesParams);
            String body = sessionStreamingNotification.composeFromProperties(locale, propertiesParams, propertiesParams);

            sendEmailService.sendEmail(userEmail, subject, body, order.getOrderData().getChannelId(),
                    EmailType.PORTAL_NOTIFICATION, null);
        } catch (IOException e) {
            LOGGER.error("[SESSION STREAMING] Error sending email to user " + userEmail, e);
            return false;
        }

        return true;
    }

    private String buildImageUrl(String file) {
        return S3URLResolver.builder()
                .withUrl(domain + fileBasePath)
                .withType(S3URLResolver.S3ImageType.CHANNEL_NOTIFICATION)
                .build()
                .buildPath(file);
    }

}
