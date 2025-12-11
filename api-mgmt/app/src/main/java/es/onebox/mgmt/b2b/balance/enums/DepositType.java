package es.onebox.mgmt.b2b.balance.enums;

import es.onebox.mgmt.datasources.api.accounting.dto.TransactionSupportType;

public enum DepositType {
    CASH(TransactionSupportType.CASH),
    TRANSFER(TransactionSupportType.WIRE),
    CHECK(TransactionSupportType.CHECK);

    private final TransactionSupportType transactionSupportType;

    DepositType(TransactionSupportType transactionSupportType) {
        this.transactionSupportType = transactionSupportType;
    }

    public TransactionSupportType getTransactionSupportType() {
        return transactionSupportType;
    }
}
