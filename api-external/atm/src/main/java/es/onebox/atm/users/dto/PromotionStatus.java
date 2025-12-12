package es.onebox.atm.users.dto;

import java.util.stream.Stream;

public enum PromotionStatus {

    AVAILABLE("Disponible"),
    PAYMENT_PENDING("En proceso de pago"),
    IN_USE("En uso");

    private static final long serialVersionUID = 1L;

    private final String name;

    PromotionStatus(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public static PromotionStatus byName(String name) {
        return Stream.of(PromotionStatus.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }
}
