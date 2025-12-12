package es.onebox.internal.sgtm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SgtmWebhookRequestDTO {

    @JsonProperty("code")
    @NotBlank(message = "Code is required")
    private String code;
    private Long entityId;
    @JsonProperty("active_external_tools")
    private List<ChannelExternalToolDTO> activeExternalTools;

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
}