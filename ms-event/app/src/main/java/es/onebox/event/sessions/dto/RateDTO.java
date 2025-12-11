package es.onebox.event.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class RateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    @JsonProperty("default")
    private boolean isDefault;
    private Integer position;

    public RateDTO() {
    }

    public RateDTO(Long id, boolean isDefault) {
        this.id = id;
        this.isDefault = isDefault;
    }

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

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
