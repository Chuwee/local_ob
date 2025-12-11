package es.onebox.mgmt.sessions.dto;

import java.io.Serializable;

public class SessionAvailabilityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int total;
    private int available;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }


}
