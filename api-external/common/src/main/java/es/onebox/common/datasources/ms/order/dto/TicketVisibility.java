package es.onebox.common.datasources.ms.order.dto;

import java.io.Serializable;

public enum TicketVisibility implements Serializable  {
    NORMAL(1),
    REDUCED(2),
    NULL(3),
    SIDE(4);

    private int id;

    private TicketVisibility(int id) {
        this.id = id;
    }


    public TicketVisibility get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }
}
