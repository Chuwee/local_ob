package es.onebox.internal.sgtm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SgtmWebhookRequestDTO {
    
    @NotBlank
    private String code;
    
    @JsonProperty("entity_id")
    private Long entityId;
    
    @JsonProperty("active_external_tools")
    private List<ChannelExternalToolDTO> activeExternalTools;
    
    @JsonProperty("provider_plan_settings")
    private String providerPlanSettings;
    
    @JsonProperty("event_id")
    private Long eventId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ChannelExternalToolDTO> getActiveExternalTools() {
        return activeExternalTools;
    }

    public void setActiveExternalTools(List<ChannelExternalToolDTO> activeExternalTools) {
        this.activeExternalTools = activeExternalTools;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getProviderPlanSettings() {
        return providerPlanSettings;
    }

    public void setProviderPlanSettings(String providerPlanSettings) {
        this.providerPlanSettings = providerPlanSettings;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
