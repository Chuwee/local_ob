package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class SessionPresaleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "id is mandatory")
    private Long id;
    private SessionPresaleStatus status;
    private List<Integer> inactiveChannels;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SessionPresaleStatus getStatus() {
        return status;
    }

    public void setStatus(SessionPresaleStatus status) {
        this.status = status;
    }

    public List<Integer> getInactiveChannels() {
        return inactiveChannels;
    }

    public void setInactiveChannels(List<Integer> inactiveChannels) {
        this.inactiveChannels = inactiveChannels;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
