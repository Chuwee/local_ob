package es.onebox.mgmt.channels.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.enums.ThankYouPageModuleTypeDTO;

public class ChannelWhitelabelThankYouPageModuleDTO {

    private ThankYouPageModuleTypeDTO type;
    @JsonProperty("text_block_id")
    private Integer textBlockId;
    private Boolean enabled;
    private Boolean visible;

    public ThankYouPageModuleTypeDTO getType() {
        return type;
    }

    public void setType(ThankYouPageModuleTypeDTO type) {
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
