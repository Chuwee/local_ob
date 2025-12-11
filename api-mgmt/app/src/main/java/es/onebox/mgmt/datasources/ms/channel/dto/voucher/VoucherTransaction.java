package es.onebox.mgmt.datasources.ms.channel.dto.voucher;

import es.onebox.mgmt.vouchers.dto.VoucherTransactionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class VoucherTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private ZonedDateTime date;
    private Double amount;
    private Double balance;
    private VoucherTransactionType type;
    private String code;

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public VoucherTransactionType getType() {
        return type;
    }

    public void setType(VoucherTransactionType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
