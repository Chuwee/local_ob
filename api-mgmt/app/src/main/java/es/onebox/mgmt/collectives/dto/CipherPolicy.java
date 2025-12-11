package es.onebox.mgmt.collectives.dto;

public enum CipherPolicy {

    NONE,
    USER,
    PASSWORD,
    USER_AND_PASSWORD,
    BIN;

    private CipherPolicy() {
    }
}
