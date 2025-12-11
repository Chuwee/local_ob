package es.onebox.event.catalog.dto;

import java.io.Serializable;

public class ChannelCatalogRateDTO implements Serializable {

    private static final long serialVersionUID = 206126509846548328L;

    private Long id;
    private String name;
    private Boolean defaultRate;
    private Integer position;

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

    public Boolean getDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(Boolean defaultRate) {
        this.defaultRate = defaultRate;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
