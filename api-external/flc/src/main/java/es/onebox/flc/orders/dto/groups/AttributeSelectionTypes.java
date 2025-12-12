package es.onebox.flc.orders.dto.groups;

import java.util.stream.Stream;

public enum AttributeSelectionTypes {
    SINGLE(0),
    MULTIPLE(1);

    private Integer id;

    private AttributeSelectionTypes(int id) {
        this.id = id;
    }

    public static AttributeSelectionTypes get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }

    public static AttributeSelectionTypes byId(Integer id) {
        return Stream.of(AttributeSelectionTypes.values())
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);
    }

}
