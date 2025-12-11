package es.onebox.mgmt.datasources.ms.channel.dto.voucher;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private Long entityId;
    private Long voucherGroupId;
    private VoucherStatus status;
    private String pin;
    private String email;
    private Double balance;
    private Integer usageUsed;
    private Integer usageLimit;
    private Boolean enableExpiration;
    private ZonedDateTime expiration;
    private List<VoucherTransaction> transactions;
    private Double consolidatedBalance;

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

    public Long getVoucherGroupId() {
        return voucherGroupId;
    }

    public void setVoucherGroupId(Long voucherGroupId) {
        this.voucherGroupId = voucherGroupId;
    }

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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getUsageUsed() {
        return usageUsed;
    }

    public void setUsageUsed(Integer usageUsed) {
        this.usageUsed = usageUsed;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Boolean getEnableExpiration() {
        return enableExpiration;
    }

    public void setEnableExpiration(Boolean enableExpiration) {
        this.enableExpiration = enableExpiration;
    }

    public ZonedDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(ZonedDateTime expiration) {
        this.expiration = expiration;
    }

    public List<VoucherTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<VoucherTransaction> transactions) {
        this.transactions = transactions;
    }

    public Double getConsolidatedBalance() {
        return consolidatedBalance;
    }

    public void setConsolidatedBalance(Double consolidatedBalance) {
        this.consolidatedBalance = consolidatedBalance;
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
