package es.onebox.mgmt.datasources.ms.event.dto.event;

public enum TimespanOrderExpire {

    DAY((byte)1),
    WEEK((byte)2),
    MONTH((byte)3);

    private byte tipo;

    TimespanOrderExpire(byte tipo) {
        this.tipo = tipo;
    }

    public byte getTipo() {
        return this.tipo;
    }

}
