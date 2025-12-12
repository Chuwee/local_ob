package es.onebox.ms.notification.providerplansettings;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

public class ProviderPlanSettingsNotificationMessage extends AbstractNotificationMessage {

    private static final long serialVersionUID = 1L;

    private Long eventId;
    private Long channelId;
    private String providerPlanSettings;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getProviderPlanSettings() {
        return providerPlanSettings;
    }

    public void setProviderPlanSettings(String providerPlanSettings) {
        this.providerPlanSettings = providerPlanSettings;
    }
}
