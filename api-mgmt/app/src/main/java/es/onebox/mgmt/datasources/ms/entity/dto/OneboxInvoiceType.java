package es.onebox.mgmt.datasources.ms.entity.dto;


import java.util.Arrays;

public enum OneboxInvoiceType {
    UNDEFINED(0), EVENT(1), CHANNEL(2);

    private final int type;

    OneboxInvoiceType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static OneboxInvoiceType fromByte(Byte value) {
        if (value == null) return null;
        return Arrays.stream(OneboxInvoiceType.values()).filter(i -> value.intValue() == i.getType()).findFirst().orElse(null);
    }
}
