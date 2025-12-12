package es.onebox.eci.ticketsales.dto;

public enum SaleType {
    SALE("01"),
    REFUND("02");

    private final String value;

    SaleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
