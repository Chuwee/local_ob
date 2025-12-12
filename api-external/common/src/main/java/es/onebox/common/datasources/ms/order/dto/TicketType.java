package es.onebox.common.datasources.ms.order.dto;

import java.io.Serializable;

public enum TicketType implements Serializable  {
    GENERAL(1),
    INVITATION(2);

    private int id;

    private TicketType(int id) {
        this.id = id;
    }


    public TicketType get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }
}
