package es.onebox.mgmt.vouchers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.LimitlessValueDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateVoucherGroupRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50, message = "name length cannot be above 50 characters")
    private String name;
    private String description;
    private VoucherStatus status;
    private UpdateVoucherGroupChannelsDTO channels;
    @JsonProperty("validation_method")
    private VoucherValidationMethod validationMethod;
    @Valid
    private RelativeTimeDTO expiration;
    @JsonProperty("usage_limit")
    private LimitlessValueDTO usageLimit;

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

    public UpdateVoucherGroupChannelsDTO getChannels() {
        return channels;
    }

    public void setChannels(UpdateVoucherGroupChannelsDTO channels) {
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
