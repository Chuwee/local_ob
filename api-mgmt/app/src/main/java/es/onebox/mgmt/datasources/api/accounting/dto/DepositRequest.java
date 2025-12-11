package es.onebox.mgmt.datasources.api.accounting.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DepositRequest extends BalanceRequest {

    private static final long serialVersionUID = 1L;

    private TransactionSupportType transactionType;
    private String transactionId;

    public TransactionSupportType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionSupportType transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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
