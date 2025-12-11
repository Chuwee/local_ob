package es.onebox.mgmt.datasources.ms.event.dto.event;

public enum SessionTypeExpiration {

    BEFORE((byte)1),
    AFTER((byte)2);

    private byte tipo;

    SessionTypeExpiration(byte tipo) {
        this.tipo = tipo;
    }

    public byte getTipo() {
        return tipo;
    }

}
