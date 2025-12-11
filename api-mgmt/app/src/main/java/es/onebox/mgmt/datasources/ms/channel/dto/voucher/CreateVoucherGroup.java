package es.onebox.mgmt.datasources.ms.channel.dto.voucher;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CreateVoucherGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Serial
    private Long entityId;
    private String name;
    private String description;
    private VoucherGroupType type;
    private VoucherValidationMethod validationMethod;
    private Long currencyId;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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

    public VoucherGroupType getType() {
        return type;
    }

    public void setType(VoucherGroupType type) {
        this.type = type;
    }

    public VoucherValidationMethod getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(VoucherValidationMethod validationMethod) {
        this.validationMethod = validationMethod;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
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
