package es.onebox.common.datasources.orders.enums;

import java.io.Serializable;

public enum PaymentType implements Serializable {
    CREDIT_CARD,
    CASH,
    OTHER,
    BANK_TRANSFER,
    CLIENT_BALANCE,
    OFFLINE,
    DIRECT_DEBIT,
    EXTERNAL
}
