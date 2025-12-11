package es.onebox.mgmt.b2b.balance.enums;

import java.util.stream.Stream;

public enum ClientTransactionField {
    ID("id"),
    TRANSACTION_CODE("transaction_code"),
    ORDER_CODE("order_code"),
    DATE("date"),
    TIME("time"),
    CHANNEL("channel"),
    USER("user"),
    NOTES("notes"),
    TRANSACTION_TYPE("transaction_type"),
    DEPOSIT_TYPE("deposit_type"),
    PREVIOUS_BALANCE("previous_balance"),
    AMOUNT("amount"),
    CREDIT("credit"),
    BALANCE("balance"),
    DEBT("debt");

    private final String code;

    ClientTransactionField(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static ClientTransactionField getByCode(final String code) {
        return Stream.of(values()).filter(field -> field.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }
}
