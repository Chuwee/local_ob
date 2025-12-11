package es.onebox.event.attendants.enums;

public enum DefaultField {
    NAME(1, 250),
    SURNAME(2, 250),
    ID(3, 20);

    private int order;
    private int maxSize;

    DefaultField(int order, int maxSize) {
        this.order = order;
        this.maxSize = maxSize;
    }

    public int getOrder() {
        return order;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
