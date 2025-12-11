package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.enums.CookiesChannelEnablingMode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class CookieSettingsBaseDTO implements Serializable {
    @Serial private static final long serialVersionUID = 2L;

    @JsonProperty("enable_custom_integration")
    private Boolean enableCustomIntegration;
    @JsonProperty("accept_integration_conditions")
    private Boolean acceptIntegrationConditions;
    @JsonProperty("channel_enabling_mode")
    private CookiesChannelEnablingMode channelEnablingMode;
    @JsonProperty("custom_integration_channels")
    private List<Long> customIntegrationChannelIds;
    @JsonProperty("date")
    private ZonedDateTime date;


    public Boolean getEnableCustomIntegration() {
        return enableCustomIntegration;
    }
    public void setEnableCustomIntegration(Boolean enableCustomIntegration) {
        this.enableCustomIntegration = enableCustomIntegration;
    }

    public Boolean getAcceptIntegrationConditions() {
        return acceptIntegrationConditions;
    }
    public void setAcceptIntegrationConditions(Boolean acceptIntegrationConditions) {
        this.acceptIntegrationConditions = acceptIntegrationConditions;
    }

    public CookiesChannelEnablingMode getChannelEnablingMode() {
        return channelEnablingMode;
    }
    public void setChannelEnablingMode(CookiesChannelEnablingMode channelEnablingMode) {
        this.channelEnablingMode = channelEnablingMode;
    }

    public List<Long> getCustomIntegrationChannelIds() {
        return customIntegrationChannelIds;
    }
    public void setCustomIntegrationChannelIds(List<Long> customIntegrationChannelIds) {
        this.customIntegrationChannelIds = customIntegrationChannelIds;
    }

    public ZonedDateTime getDate() {
        return date;
    }
    public void setDate(ZonedDateTime date) {
        this.date = date;
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
