package es.onebox.mgmt.datasources.ms.ticket;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketNotNumberedZoneLink implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long id;
    @NotNull
    private Integer capacity;

    public SeasonTicketNotNumberedZoneLink(@NotNull Long id, @NotNull Integer capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}