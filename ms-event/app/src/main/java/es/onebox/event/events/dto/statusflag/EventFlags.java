package es.onebox.event.events.dto.statusflag;

import java.io.Serializable;

public class EventFlags implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private EventSaleFlag sale;
    private EventReleaseFlag release;

    public EventSaleFlag getSale() {
        return sale;
    }

    public void setSale(EventSaleFlag sale) {
        this.sale = sale;
    }

    public EventReleaseFlag getRelease() {
        return release;
    }

    public void setRelease(EventReleaseFlag release) {
        this.release = release;
    }

}
