package es.onebox.mgmt.events.dto.channel;

import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class BaseEventChannelDTO implements Serializable, DateConvertible {

    private static final long serialVersionUID = 1L;

    private EventInfoDTO event;
    private EventChannelInfoDTO channel;
    private EventChannelStatusInfoDTO status;
    private EventChannelSettingsDTO settings;
    private ProviderPlanSettingsDTO providerPlanSettings;

    public EventChannelInfoDTO getChannel() {
        return channel;
    }

    public void setChannel(EventChannelInfoDTO channel) {
        this.channel = channel;
    }

    public EventChannelStatusInfoDTO getStatus() {
        return status;
    }

    public void setStatus(EventChannelStatusInfoDTO status) {
        this.status = status;
    }

    public EventChannelSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(EventChannelSettingsDTO settings) {
        this.settings = settings;
    }

    public EventInfoDTO getEvent() {
        return event;
    }

    public void setEvent(EventInfoDTO event) {
        this.event = event;
    }

    public ProviderPlanSettingsDTO getProviderPlanSettings() {
        return providerPlanSettings;
    }

    public void setProviderPlanSettings(ProviderPlanSettingsDTO providerPlanSettings) {
        this.providerPlanSettings = providerPlanSettings;
    }

    @Override
    public void convertDates() {
        if (settings != null) {
            settings.convertDates();
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
}
