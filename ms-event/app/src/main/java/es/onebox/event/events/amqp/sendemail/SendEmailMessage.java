package es.onebox.event.events.amqp.sendemail;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

import java.util.Map;
import java.util.Objects;

public class SendEmailMessage extends AbstractNotificationMessage implements NotificationMessage {

    private static final long serialVersionUID = 1L;

    private String orderCode;
    private String subject;
    private String body;
    private String targetEmail;
    private Integer channelId;
    private Integer mailTemplateId;
    private EmailType emailType;

    private Map<String, byte[]> attachments;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getMailTemplateId() {
        return mailTemplateId;
    }

    public void setMailTemplateId(Integer mailTemplateId) {
        this.mailTemplateId = mailTemplateId;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    public Map<String, byte[]> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, byte[]> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SendEmailMessage that = (SendEmailMessage) o;
        return Objects.equals(orderCode, that.orderCode) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(body, that.body) &&
                Objects.equals(targetEmail, that.targetEmail) &&
                Objects.equals(channelId, that.channelId) &&
                Objects.equals(mailTemplateId, that.mailTemplateId) &&
                emailType == that.emailType &&
                Objects.equals(attachments, that.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderCode, subject, body, targetEmail, channelId, mailTemplateId, emailType, attachments);
    }

    @Override
    public String toString() {
        return "SendEmailMessage{" +
                "orderCode='" + orderCode + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", targetEmail='" + targetEmail + '\'' +
                ", channelId=" + channelId +
                ", mailTemplateId=" + mailTemplateId +
                ", emailType=" + emailType +
                ", attachments=" + attachments +
                '}';
    }
}
