package es.onebox.mgmt.b2b.clients.enums;

public enum ClientType {
    CLIENT_B2B(1),
    CLIENT_B2C(2);

    private final Integer id;

    ClientType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
