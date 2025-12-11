package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SessionPackDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private SessionPackDateDTO date;
    private List<Long> sessionIds;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getSessionIds() { return sessionIds; }

    public void setSessionIds(List<Long> sessionIds) { this.sessionIds = sessionIds; }

    public SessionPackDateDTO getDate() { return date; }

    public void setDate(SessionPackDateDTO date) { this.date = date; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
