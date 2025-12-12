package es.onebox.common.datasources.ms.order.dto;

public enum AttributeScopes {
    EVENT(0),
    SESSION(1),
    GROUP(2),
    PROFILE(3);

    private int id;

    private AttributeScopes(int id) {
        this.id = id;
    }

    public static AttributeScopes get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }

}
