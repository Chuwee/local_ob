package es.onebox.event.common.amqp.webhook;

import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

public class WebhookEventGenericNotificationMessage extends AbstractNotificationMessage implements NotificationMessage {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String event;
    private Long channelId;
    private Long templateId;
    private Long rateId;
    private NotificationSubtype notificationSubtype;


    public WebhookEventGenericNotificationMessage(Long id, String event, Long channelId, Long templateId, Long rateId, NotificationSubtype notificationSubtype) {
        this.id = id;
      this.event = event;
      this.channelId = channelId;
      this.templateId = templateId;
      this.rateId = rateId;
      this.notificationSubtype = notificationSubtype;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public NotificationSubtype getNotificationSubtype() {
        return notificationSubtype;
    }

    public void setNotificationSubtype(NotificationSubtype notificationSubtype) {
        this.notificationSubtype = notificationSubtype;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
