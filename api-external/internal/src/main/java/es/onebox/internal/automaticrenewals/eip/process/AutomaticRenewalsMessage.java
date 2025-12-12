package es.onebox.internal.automaticrenewals.eip.process;

import es.onebox.internal.automaticrenewals.renewals.enums.AutomaticRenewalsProviderType;
import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import java.io.Serial;

public class AutomaticRenewalsMessage extends AbstractNotificationMessage {

    @Serial
    private static final long serialVersionUID = -2378262539873309306L;

    private Long seasonTicketId;
    private Long channelId;
    private AutomaticRenewalsProviderType providerType;
    private Object data;

    public AutomaticRenewalsMessage() {}

    public AutomaticRenewalsMessage(Long seasonTicketId, Long channelId, AutomaticRenewalsProviderType providerType, Object data) {
        this.seasonTicketId = seasonTicketId;
        this.channelId = channelId;
        this.providerType = providerType;
        this.data = data;
    }

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public AutomaticRenewalsProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(AutomaticRenewalsProviderType providerType) {
        this.providerType = providerType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static class Builder {

        private Long seasonTicketId;
        private Long channelId;
        private AutomaticRenewalsProviderType providerType;
        private Object data;

        public Builder seasonTicketId(Long seasonTicketId) {
            this.seasonTicketId = seasonTicketId;
            return this;
        }

        public Builder channelId(Long channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder providerType(AutomaticRenewalsProviderType providerType) {
            this.providerType = providerType;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public AutomaticRenewalsMessage build() {
            return new AutomaticRenewalsMessage(seasonTicketId, channelId, providerType, data);

        }
    }
}
