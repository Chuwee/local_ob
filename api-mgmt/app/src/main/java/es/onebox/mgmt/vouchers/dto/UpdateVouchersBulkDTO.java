package es.onebox.mgmt.vouchers.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class UpdateVouchersBulkDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "Codes must not be empty or null")
    @Size(min = 1, max = 50000, message = "The number of vouchers to update must be between 1 and 50000")
    private List<String> codes;

    private VoucherStatus status;

    private VoucherUsageDTO usage;

    private ZonedDateTime expiration;

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public VoucherStatus getStatus() {
        return status;
    }

    public void setStatus(VoucherStatus status) {
        this.status = status;
    }

    public VoucherUsageDTO getUsage() {
        return usage;
    }

    public void setUsage(VoucherUsageDTO usage) {
        this.usage = usage;
    }

    public ZonedDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(ZonedDateTime expiration) {
        this.expiration = expiration;
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
