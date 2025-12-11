package es.onebox.mgmt.channels.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class AccessUsersDatesFilterDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "The user field cannot be null")
    private String user;
    @NotNull(message = "The date field cannot be null")
    private ZonedDateTime date;

    public String getUser() { return user; }

    public void setUser(String user) { this.user = user; }

    public ZonedDateTime getDate() { return date; }

    public void setDate(ZonedDateTime date) { this.date = date; }
}
