package es.onebox.event.common.enums;

public enum ElementType {
    TEXT(1),
    LOGO(2),
    IMAGEN(3),
    VIDEO(4),
    FILE(5);

    private Integer id;

    private ElementType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
