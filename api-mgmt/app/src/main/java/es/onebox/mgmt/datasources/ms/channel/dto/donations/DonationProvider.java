package es.onebox.mgmt.datasources.ms.channel.dto.donations;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DonationProvider implements Serializable {
    @Serial
    private static final long serialVersionUID = 232124482913513804L;

    private Long id;
    private String targetId;
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
