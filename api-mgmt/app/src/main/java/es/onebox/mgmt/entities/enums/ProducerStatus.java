package es.onebox.mgmt.entities.enums;

import java.io.Serializable;

public enum ProducerStatus implements Serializable {

    ACTIVE(1),
    INACTIVE(2);

    private int state;

    private ProducerStatus(int state) {
        this.state = state;
    }

    public int getState() {
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
