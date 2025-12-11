package es.onebox.mgmt.channels.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class AccessUsersDatesFilter implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String user;
    private ZonedDateTime date;

    public AccessUsersDatesFilter(String user, ZonedDateTime date) {
        this.user = user;
        this.date = date;
    }

    public String getUser() { return user; }

    public void setUser(String user) { this.user = user; }

    public ZonedDateTime getDate() { return date; }

    public void setDate(ZonedDateTime date) { this.date = date; }
}
