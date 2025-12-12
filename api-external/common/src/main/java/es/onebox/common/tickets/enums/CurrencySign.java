package es.onebox.common.tickets.enums;

public enum CurrencySign {
    EUR("€"),
    GPB("£"),
    USD("$");

    private String sign;

    CurrencySign(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }
}
