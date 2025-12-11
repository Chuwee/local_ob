package es.onebox.mgmt.vouchers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.LimitlessValueDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class CreateVoucherRequestDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    private String pin;

    private String email;

    @NotNull(message = "balance is mandatory")
    @Min(value = 0, message = "balance must be equal or above 0")
    private Double balance;

    @JsonProperty("usage_limit")
    private LimitlessValueDTO usageLimit;

    private ZonedDateTime expiration;

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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public LimitlessValueDTO getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(LimitlessValueDTO usageLimit) {
        this.usageLimit = usageLimit;
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
