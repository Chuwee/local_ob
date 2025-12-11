package es.onebox.event.products.enums;

import java.util.Arrays;

public enum ProductState {
    DELETED(0),
    ACTIVE(1),
    INACTIVE(2);
    private final int state;

    ProductState(int state) {
        this.state = state;
    }

    public int getId() {
        return state;
    }

    public static ProductState get(int id) {
        return Arrays.stream(values()).filter(t -> t.getId() == id).findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String toString() {
        return String.valueOf(state);
    }

}
