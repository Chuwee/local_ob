package es.onebox.mgmt.datasources.api.accounting.dto;

import java.io.Serializable;
import java.util.List;

public class TransactionAudits implements Serializable {
    private List<TransactionAudit> transactionAudits;
    private long totalElements;


    public List<TransactionAudit> getTransactionAudits() {
        return transactionAudits;
    }

    public void setTransactionAudits(List<TransactionAudit> transactionAudits) {
        this.transactionAudits = transactionAudits;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
