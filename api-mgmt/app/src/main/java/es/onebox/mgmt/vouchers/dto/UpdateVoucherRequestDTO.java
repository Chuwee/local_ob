package es.onebox.mgmt.vouchers.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateVoucherRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private VoucherStatus status;

    private String pin;

    private String email;

    private VoucherUsageDTO usage;

    private VoucherExpirationDTO expiration;

    public VoucherStatus getStatus() {
        return status;
    }

    public void setStatus(VoucherStatus status) {
        this.status = status;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public VoucherUsageDTO getUsage() {
        return usage;
    }

    public void setUsage(VoucherUsageDTO usage) {
        this.usage = usage;
    }

    public VoucherExpirationDTO getExpiration() {
        return expiration;
    }

    public void setExpiration(VoucherExpirationDTO expiration) {
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
