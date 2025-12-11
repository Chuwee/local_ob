package es.onebox.mgmt.datasources.ms.entity.enums;

public enum ProducerStatus {

    DELETED(0),
    ACTIVE(1),
    INACTIVE(2);

    private int status;

    private ProducerStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static ProducerStatus get(int id) {
        return values()[id];
    }

    @Override
    public String toString() {
        return String.valueOf(status);
    }

}
