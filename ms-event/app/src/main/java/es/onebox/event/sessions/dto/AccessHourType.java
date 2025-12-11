package es.onebox.event.sessions.dto;

public enum AccessHourType {

    AUTOMATIC(1),
    SPECIFIC(2);

    private Integer id;

    private AccessHourType(int id) {
        this.id = id;
    }

    public static AccessHourType get(int id) {
        return values()[id - 1];
    }

    public int getId() {
        return this.id;
    }
}
