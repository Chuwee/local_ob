package es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings;

import es.onebox.mgmt.datasources.ms.channel.enums.ThankYouPageModuleType;

public class ChannelWhitelabelThankYouPageModule {

    private ThankYouPageModuleType type;
    private Integer textBlockId;
    private Boolean enabled;
    private Boolean visible;

    public ThankYouPageModuleType getType() {
        return type;
    }

    public void setType(ThankYouPageModuleType type) {
        this.type = type;
    }

    public Integer getTextBlockId() {
        return textBlockId;
    }

    public void setTextBlockId(Integer textBlockId) {
        this.textBlockId = textBlockId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
