package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DonationProviderDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2307256345549009853L;

    @JsonProperty("id")
    private Long id;
    @JsonProperty("target_id")
    private String targetId;
    @JsonProperty("additional_properties")
    private List<Map<String, Object>> additionalProperties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public List<Map<String, Object>> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<Map<String, Object>> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
