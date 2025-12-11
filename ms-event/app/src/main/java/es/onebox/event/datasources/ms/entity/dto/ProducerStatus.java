package es.onebox.event.datasources.ms.entity.dto;

public enum ProducerStatus {
    DELETED(0),
    ACTIVE(1),
    INACTIVE(2);

    private int state;

    private ProducerStatus(int state) {
        this.state = state;
    }

    public int getId() {
        return state;
    }

    public static ProducerStatus get(int id) {
        return values()[id];
    }

    @Override
    public String toString() {
        return String.valueOf(state);
    }

}
