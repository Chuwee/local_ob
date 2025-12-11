package es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals;

public final class TerminalUpdateRequest {
    private String name;
    private Long entityId;
    private Boolean licenseEnabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Boolean getLicenseEnabled() {
        return licenseEnabled;
    }

    public void setLicenseEnabled(Boolean licenseEnabled) {
        this.licenseEnabled = licenseEnabled;
    }

}
