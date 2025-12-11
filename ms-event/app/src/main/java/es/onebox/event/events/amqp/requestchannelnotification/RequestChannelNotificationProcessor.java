package es.onebox.event.events.amqp.requestchannelnotification;


import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.events.amqp.sendemail.EmailType;
import es.onebox.event.events.amqp.sendemail.SendEmailService;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.record.MailReceiverUserRecord;
import es.onebox.event.events.dao.record.MailSenderUserRecord;
import es.onebox.event.events.domain.NotificationType;
import es.onebox.event.user.dao.UserDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static es.onebox.core.utils.common.CommonUtils.isNull;

@Component
public class RequestChannelNotificationProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestChannelNotificationProcessor.class);

    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    @Qualifier("sendEmailProducer")
    private DefaultProducer sendEmailProducer;
    @Autowired
    private EventDao eventDao;
    @Autowired
    private SendEmailService sendEmailService;

    @Value("${onebox.s3.ms-event.hostname}")
    private String domain;
    @Value("${onebox.s3.ms-event.mail.request-channel-notification.path}")
    private String fileBasePath;

    @Autowired
    @Qualifier("requestChannelNotification")
    private HandlebarComposer handlebarComposer;

    @Override
    public void execute(Exchange exchange) throws Exception {
        RequestChannelNotificationMessage message = exchange.getIn().getBody(RequestChannelNotificationMessage.class);
        CpanelCanalRecord channel = channelDao.getById(message.getChannelId());
        if (isNull(channel)) {
            error("it doesn't seem to exist", message);
            return;
        }
        List<MailReceiverUserRecord> receivers = userDao.getUsersToNotify(NotificationType.REQUEST_TO_CHANNEL, channel.getIdentidad());
        if (isNull(receivers) || receivers.isEmpty()) {
            return;
        }
        MailSenderUserRecord sender = userDao.getMailSender(message.getUserId());
        if (isNull(sender)) {
            error("sender user doesn't seem to exist", message);
            return;
        }
        CpanelEventoRecord event = eventDao.getById(message.getEventId());
        if (isNull(event)) {
            error("event doesn't seem to exist", message);
            return;
        }
        notifyUsers(receivers, sender, event.getNombre());
    }

    private void notifyUsers(List<MailReceiverUserRecord> receivers, MailSenderUserRecord sender, String eventName) throws IOException {
        Map<String, Object> propertiesParams = new HashMap<>();
        Map<String, Object> handlebarsParams = new HashMap<>();
        propertiesParams.put("eventName", eventName);
        propertiesParams.put("senderName", sender.getName());
        propertiesParams.put("entityName", sender.getEntityName());

        for (MailReceiverUserRecord receiver : receivers) {
            Locale locale = isNull(receiver.getLocale()) ? null : LocaleUtils.toLocale(receiver.getLocale());
            handlebarsParams.put("FOOT_IMAGE", buildImageUrl(locale, "foot"));
            handlebarsParams.put("HEAD_IMAGE", buildImageUrl(locale, "head"));
            propertiesParams.put("receiverName", receiver.getName());
            String subject = handlebarComposer.getPropertiesMessage(locale, "SUBJECT");
            String body = handlebarComposer.composeFromProperties(locale, propertiesParams, handlebarsParams);
            sendEmailService.sendEmail(receiver.getEmail(), subject, body, null, EmailType.BACKEND_NOTIFICATION, null);
        }
    }

    private void error(String msg, RequestChannelNotificationMessage message) {
        LOGGER.error("[REQUEST CHANNEL NOTIFICATION] Error notifying channel: {}. ChannelId: {}, EventId: {}, UserId: {}",
                msg, message.getChannelId(), message.getEventId(), message.getUserId());
    }

    private String buildImageUrl(Locale locale, String file) {
        return S3URLResolver.builder()
                .withUrl(domain + fileBasePath)
                .withType(S3URLResolver.S3ImageType.CHANNEL_NOTIFICATION)
                .build()
                .buildPath(file + "_" + locale + ".jpg");
    }

}
