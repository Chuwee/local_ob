package es.onebox.common.datasources.common.dto;

import es.onebox.common.datasources.common.enums.SessionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Session implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private SessionDate date;

    private SessionType type;

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

    public SessionDate getDate() {
        return date;
    }

    public void setDate(SessionDate date) {
        this.date = date;
    }

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
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
