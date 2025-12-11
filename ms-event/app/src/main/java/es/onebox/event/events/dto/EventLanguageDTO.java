package es.onebox.event.events.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class EventLanguageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String code;

    @JsonProperty("isDefault")
    private Boolean isDefault;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonIgnore
    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
