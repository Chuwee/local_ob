package es.onebox.mgmt.terminals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.TerminalType;

public class TerminalSearchFilterDTO extends BaseRequestFilter {
    @JsonProperty("entity_id")
    private Long entityId;
    private TerminalType type;
    @JsonProperty("license_enabled")
    private Boolean licenseEnabled;
    private String q;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public TerminalType getType() {
        return type;
    }

    public void setType(TerminalType type) {
        this.type = type;
    }

    public Boolean getLicenseEnabled() {
        return licenseEnabled;
    }

    public void setLicenseEnabled(Boolean licenseEnabled) {
        this.licenseEnabled = licenseEnabled;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }
}
