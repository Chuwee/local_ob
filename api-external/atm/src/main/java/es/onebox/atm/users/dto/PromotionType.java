package es.onebox.atm.users.dto;

public enum PromotionType {

    WALLET("Tarjeta monedero"),
    ONESHOT("otra");

    private static final long serialVersionUID = 1L;

    private final String name;

    PromotionType(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    // TODO pendiente de saber todos los valores posibles para acabar el enum y usar este método
    /*public static PromotionType byName(String name) {
        return Stream.of(PromotionType.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }*/
}
