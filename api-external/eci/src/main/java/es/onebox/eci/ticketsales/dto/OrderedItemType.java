package es.onebox.eci.ticketsales.dto;

public enum OrderedItemType {
    TICKET("00"),
    CHARGE("01");

    private final String value;

    OrderedItemType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
