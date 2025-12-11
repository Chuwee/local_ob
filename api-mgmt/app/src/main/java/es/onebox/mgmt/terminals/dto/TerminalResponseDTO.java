package es.onebox.mgmt.terminals.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.TerminalType;

import java.io.Serializable;

public class TerminalResponseDTO implements Serializable {

    private Long id;
    private String code;
    private String name;
    private TerminalType type;
    private IdNameDTO entity;
    private Boolean online;
    private LicenseDTO license;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public LicenseDTO getLicense() {
        return license;
    }

    public void setLicense(LicenseDTO licenseDTO) {
        this.license = licenseDTO;
    }
}
