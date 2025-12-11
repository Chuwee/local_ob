package es.onebox.mgmt.vouchers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CreateVoucherGroupRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @JsonProperty(value = "entity_id")
    private Long entityId;

    @NotBlank(message = "name is mandatory")
    @Size(max = 50, message = "name length cannot be above 50 characters")
    private String name;

    @NotNull(message = "validation_method is mandatory")
    @JsonProperty("validation_method")
    private VoucherValidationMethod validationMethod;

    private String description;

    @JsonProperty("currency_code")
    private String currencyCode;

    @NotNull(message = "type is mandatory")
    private VoucherGroupType type;


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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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
