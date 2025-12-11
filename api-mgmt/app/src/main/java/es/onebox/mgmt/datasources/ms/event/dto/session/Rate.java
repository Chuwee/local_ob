package es.onebox.mgmt.datasources.ms.event.dto.session;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Rate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    @JsonProperty("default")
    private boolean defaultRate;

    public Rate() {
    }

    public Rate(Long id, boolean defaultRate) {
        this.id = id;
        this.defaultRate = defaultRate;
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

    public boolean isDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(boolean defaultRate) {
        this.defaultRate = defaultRate;
    }
}
