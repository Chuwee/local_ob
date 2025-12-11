package es.onebox.mgmt.vouchers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.LimitlessValueDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class VoucherGroupDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    @Size(max = 200, message = "name must be below 50 characters")
    private String name;
    @Size(max = 200, message = "description must be below 200 characters")
    private String description;
    private VoucherStatus status;
    private VoucherGroupType type;
    @JsonProperty("usage_limit")
    private LimitlessValueDTO usageLimit;
    private IdNameDTO entity;
    @Valid
    private VoucherGroupChannelsDTO channels;
    @JsonProperty("validation_method")
    private VoucherValidationMethod validationMethod;
    private RelativeTimeDTO expiration;
    @JsonProperty("currency_code")
    private String currencyCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public VoucherStatus getStatus() {
        return status;
    }

    public void setStatus(VoucherStatus status) {
        this.status = status;
    }

    public VoucherGroupType getType() {
        return type;
    }

    public void setType(VoucherGroupType type) {
        this.type = type;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public VoucherGroupChannelsDTO getChannels() {
        return channels;
    }

    public void setChannels(VoucherGroupChannelsDTO channels) {
        this.channels = channels;
    }

    public VoucherValidationMethod getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(VoucherValidationMethod validationMethod) {
        this.validationMethod = validationMethod;
    }

    public RelativeTimeDTO getExpiration() {
        return expiration;
    }

    public void setExpiration(RelativeTimeDTO expiration) {
        this.expiration = expiration;
    }

    public LimitlessValueDTO getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(LimitlessValueDTO usageLimit) {
        this.usageLimit = usageLimit;
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
