package es.onebox.event.sessions.enums;

import java.util.Arrays;

public enum TaxType {

    TICKET(0),
    TICKET_INVITATION(1),
    PRODUCT(2),
    CHARGES(3),
    FEVER_API(4);

    private Integer type;

    TaxType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.valueOf(type);
    }

    public static TaxType getFromInteger(Integer type) {
        if (type == null) return null;
        return Arrays.stream(TaxType.values()).filter(i -> i.type.equals(type)).findFirst().orElse(null);
    }

}
