package es.onebox.event.products.enums;

import java.util.Arrays;

public enum SelectionType {

    ALL(0),
    RESTRICTED(1);

    private final Integer id;

    SelectionType(Integer id) {
        this.id = id;
    }

    public static SelectionType get(int id) {
        return Arrays.stream(values()).filter(t -> t.getId() == id).findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public Integer getId() {
        return id;
    }
}
