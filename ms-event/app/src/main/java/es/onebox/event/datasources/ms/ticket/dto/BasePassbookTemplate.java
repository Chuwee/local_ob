package es.onebox.event.datasources.ms.ticket.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

import es.onebox.event.datasources.ms.ticket.enums.PassbookDesign;

public class BasePassbookTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long entityId;
    private Long operatorId;
    private String code;
    private String name;
    private String description;
    private PassbookDesign passbookDesign;
    private String defaultLanguage;
    private Boolean defaultPassbook;
    private Boolean obfuscateBarcode;
    private ZonedDateTime createDate;
    private ZonedDateTime updateDate;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public Boolean getDefaultPassbook() {
        return defaultPassbook;
    }

    public void setDefaultPassbook(Boolean defaultPassbook) {
        this.defaultPassbook = defaultPassbook;
    }

    public Boolean getObfuscateBarcode() {
        return obfuscateBarcode;
    }

    public void setObfuscateBarcode(Boolean obfuscateBarcode) {
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
}
