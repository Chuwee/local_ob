package es.onebox.event.catalog.dto.presales;


import java.util.Arrays;

public enum PresaleValidatorType {

    COLLECTIVE(1),
    CUSTOMERS(2);

    private final Integer id;

    PresaleValidatorType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PresaleValidatorType getById(Integer id) {
        return Arrays.stream(values())
                .filter( item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
