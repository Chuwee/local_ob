package es.onebox.mgmt.sessions.dto;

public enum PackBlockingAction {

    RELEASE(1),
    KEEP(2);

    private final Integer id;

    PackBlockingAction(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

}
