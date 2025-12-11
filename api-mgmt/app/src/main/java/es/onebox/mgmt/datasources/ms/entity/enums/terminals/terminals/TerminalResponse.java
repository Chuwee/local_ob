package es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals;

import es.onebox.mgmt.datasources.ms.entity.enums.terminals.TerminalState;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.TerminalType;

import java.time.ZonedDateTime;

public class TerminalResponse extends BaseTerminal {

    private String name;
    private TerminalType type;
    private Long entityId;
    private String entityName;
    private Boolean online;
    private TerminalState licenseState;
    private ZonedDateTime licenseActivationDate;
    private ZonedDateTime licenseExpirationDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TerminalType getType() {
        return type;
    }

    public void setType(TerminalType type) {
        this.type = type;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public TerminalState getLicenseState() {
        return licenseState;
    }

    public void setLicenseState(TerminalState licenseState) {
        this.licenseState = licenseState;
    }

    public ZonedDateTime getLicenseActivationDate() {
        return licenseActivationDate;
    }

    public void setLicenseActivationDate(ZonedDateTime licenseActivationDate) {
        this.licenseActivationDate = licenseActivationDate;
    }

    public ZonedDateTime getLicenseExpirationDate() {
        return licenseExpirationDate;
    }

    public void setLicenseExpirationDate(ZonedDateTime licenseExpirationDate) {
        this.licenseExpirationDate = licenseExpirationDate;
    }
}
