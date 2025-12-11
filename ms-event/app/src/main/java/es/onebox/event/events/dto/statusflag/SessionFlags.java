package es.onebox.event.events.dto.statusflag;

import java.io.Serializable;

public class SessionFlags implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private SessionSaleFlagStatus sale;
    private SessionReleaseFlagStatus release;

    public SessionSaleFlagStatus getSale() {
        return sale;
    }

    public void setSale(SessionSaleFlagStatus sale) {
        this.sale = sale;
    }

    public SessionReleaseFlagStatus getRelease() {
        return release;
    }

    public void setRelease(SessionReleaseFlagStatus release) {
        this.release = release;
    }

}
