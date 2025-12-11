package es.onebox.mgmt.users.enums;

public enum MFAType {
    DISABLED(0),
    EMAIL(1);

    private final Integer id;

    MFAType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
