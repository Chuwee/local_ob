package es.onebox.ms.notification.webhooks.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.ms.notification.datasources.ms.channel.dto.ChannelExternalToolDTO;

import java.io.Serializable;
import java.util.ArrayList;

public class ExternalApiWebhookDto implements Serializable {

    private String code;
    private Long entityId;
    @JsonProperty("active_external_tools")
    private ArrayList<ChannelExternalToolDTO> activeExternalTools;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<ChannelExternalToolDTO> getActiveExternalTools() {
        return activeExternalTools;
    }

    public void setActiveExternalTools(ArrayList<ChannelExternalToolDTO> activeExternalTools) {
        this.activeExternalTools = activeExternalTools;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
