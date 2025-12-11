package es.onebox.mgmt.datasources.ms.ticket.dto;

import es.onebox.mgmt.datasources.ms.ticket.enums.PassbookTemplateType;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class BasePassbookTemplate implements Serializable {

    private Long entityId;
    private Long operatorId;
    private PassbookTemplateType type;
    private String code;
    private String name;
    private String description;
    private String defaultLanguage;
    private PassbookDesign passbookDesign;
    private boolean defaultPassbook;
    private boolean obfuscateBarcode;
    private ZonedDateTime createDate;
    private ZonedDateTime updateDate;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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

    public boolean isDefaultPassbook() {
        return defaultPassbook;
    }

    public void setDefaultPassbook(boolean defaultPassbook) {
        this.defaultPassbook = defaultPassbook;
    }

    public boolean isObfuscateBarcode() {
        return obfuscateBarcode;
    }

    public void setObfuscateBarcode(boolean obfuscateBarcode) {
        this.obfuscateBarcode = obfuscateBarcode;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    public ZonedDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(ZonedDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PassbookDesign getPassbookDesign() {
        return passbookDesign;
    }

    public void setPassbookDesign(PassbookDesign passbookDesign) {
        this.passbookDesign = passbookDesign;
    }

    public PassbookTemplateType getType() {
        return type;
    }

    public void setType(PassbookTemplateType type) {
        this.type = type;
    }
}
