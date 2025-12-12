package es.onebox.atm.users.dto;

public enum DiscountType {

    FIXED("Numerico"),
    PERCENTAGE("Otro");

    private static final long serialVersionUID = 1L;

    private final String name;

    DiscountType(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    // TODO pendiente de saber todos los valores posibles para acabar el enum y usar este método
    /*public static DiscountType byName(String name) {
        return Stream.of(DiscountType.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }*/
}
