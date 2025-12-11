package es.onebox.mgmt.passbook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class BasePassbookTemplateDTO implements Serializable {

    @JsonProperty("entity_id")
    private Long entityId;
    @JsonProperty("operator_id")
    private Long operatorId;
    private PassbookTemplateType type;
    private String code;
    private String name;
    private String description;
    @JsonProperty("passbook_design")
    private PassbookDesignDTO passbookDesign;
    @JsonProperty("default_passbook")
    private boolean defaultPassbook;
    @JsonProperty("obfuscated_barcode")
    private boolean obfuscateBarcode;
    @JsonProperty("create_date")
    private ZonedDateTime createDate;
    @JsonProperty("update_date")
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

    public ZonedDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(ZonedDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PassbookDesignDTO getPassbookDesign() {
        return passbookDesign;
    }

    public void setPassbookDesign(PassbookDesignDTO passbookDesign) {
        this.passbookDesign = passbookDesign;
    }

    public PassbookTemplateType getType() {
        return type;
    }

    public void setType(PassbookTemplateType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
