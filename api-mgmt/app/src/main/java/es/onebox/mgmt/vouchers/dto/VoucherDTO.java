package es.onebox.mgmt.vouchers.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.List;

public class VoucherDTO implements Serializable, DateConvertible {

    private static final long serialVersionUID = 1L;

    private String code;
    @JsonProperty("voucher_group")
    private IdNameDTO voucherGroup;
    private VoucherStatus status;
    private String pin;
    private String email;
    private Double balance;
    @JsonProperty("consolidated_balance")
    private Double consolidatedBalance;
    private VoucherUsageDTO usage;
    private VoucherExpirationDTO expiration;
    private List<VoucherTransactionDTO> transactions;

    @JsonIgnore
    private String operatorTZ;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public IdNameDTO getVoucherGroup() {
        return voucherGroup;
    }

    public void setVoucherGroup(IdNameDTO voucherGroup) {
        this.voucherGroup = voucherGroup;
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

    public VoucherStatus getStatus() {
        return status;
    }

    public void setStatus(VoucherStatus status) {
        this.status = status;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
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

    public List<VoucherTransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<VoucherTransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public String getOperatorTZ() {
        return operatorTZ;
    }

    public void setOperatorTZ(String operatorTZ) {
        this.operatorTZ = operatorTZ;
    }

    public Double getConsolidatedBalance() {
        return consolidatedBalance;
    }

    public void setConsolidatedBalance(Double consolidatedBalance) {
        this.consolidatedBalance = consolidatedBalance;
    }

    @Override
    public void convertDates() {
        if (operatorTZ != null) {
            if (expiration != null && expiration.getDate() != null) {
                expiration.setDate(expiration.getDate().withZoneSameInstant(ZoneId.of(operatorTZ)).withNano(0));
            }
            if (!CollectionUtils.isEmpty(transactions)) {
                for (VoucherTransactionDTO transaction : transactions) {
                    transaction.setDate(transaction.getDate().withZoneSameInstant(ZoneId.of(operatorTZ)).withNano(0));
                }
            }
        }
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
