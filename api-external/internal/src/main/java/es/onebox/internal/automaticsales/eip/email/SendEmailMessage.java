package es.onebox.internal.automaticsales.eip.email;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.util.Collections;
import java.util.Map;

public class SendEmailMessage extends AbstractNotificationMessage {

    @Serial
    private static final long serialVersionUID = 4481966520047488501L;
    private String subject;
    private String body;
    private String targetEmail;
    private Integer channelId;
    private Integer mailTemplateId;
    private Map<String, byte[]> attachments;

    private SendEmailMessage(Builder builder) {
        this.subject = builder.subject;
        this.body = builder.body;
        this.targetEmail = builder.targetEmail;
        this.channelId = builder.channelId;
        this.mailTemplateId = builder.mailTemplateId;
        this.attachments = builder.attachments;
    }

    public SendEmailMessage() {
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

    public Map<String, byte[]> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, byte[]> attachments) {
        this.attachments = attachments;
    }

    public Integer getMailTemplateId() {
        return mailTemplateId;
    }

    public void setMailTemplateId(Integer mailTemplateId) {
        this.mailTemplateId = mailTemplateId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String subject;
        private String body;
        private String targetEmail;
        private Integer channelId;
        private Integer mailTemplateId;
        private Map<String, byte[]> attachments = Collections.emptyMap();

        private Builder() {
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder targetEmail(String targetEmail) {
            this.targetEmail = targetEmail;
            return this;
        }

        public Builder channelId(Integer channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder mailTemplateId(Integer mailTemplateId) {
            this.mailTemplateId = mailTemplateId;
            return this;
        }

        public Builder attachments(Map<String, byte[]> attachments) {
            this.attachments = attachments;
            return this;
        }

        public SendEmailMessage build() {
            return new SendEmailMessage(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
