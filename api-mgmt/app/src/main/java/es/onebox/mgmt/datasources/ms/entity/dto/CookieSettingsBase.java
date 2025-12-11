package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.datasources.ms.entity.enums.CookiesChannelEnablingMode;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class CookieSettingsBase implements Serializable {
    @Serial private static final long serialVersionUID = 2L;

    private Boolean enableCustomIntegration;
    private Boolean acceptIntegrationConditions;
    private CookiesChannelEnablingMode channelEnablingMode;
    private List<Long> customIntegrationChannelIds;
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
}
