package es.onebox.mgmt.users.enums;

public enum VisibilityType {
    ALL(0),
    RESTRICTED(1);

    private final Integer id;

    VisibilityType(Integer id) {this.id = id;}

    public Integer getId() {
        return id;
    }
}
