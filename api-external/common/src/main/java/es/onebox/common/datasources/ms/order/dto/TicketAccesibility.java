package es.onebox.common.datasources.ms.order.dto;

import java.io.Serializable;

public enum TicketAccesibility implements Serializable  {
    NORMAL(1),
    DISABILITY(2);

    private int id;

    private TicketAccesibility(int id) {
        this.id = id;
    }


    public TicketAccesibility get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }
}
