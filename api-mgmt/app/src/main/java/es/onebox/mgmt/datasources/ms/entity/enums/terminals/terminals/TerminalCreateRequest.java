package es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals;

import es.onebox.mgmt.datasources.ms.entity.enums.terminals.TerminalType;

public final class TerminalCreateRequest {

    private String name;
    private String code;
    private Long entityId;
    private TerminalType type;
    private Boolean licenseEnabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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
}