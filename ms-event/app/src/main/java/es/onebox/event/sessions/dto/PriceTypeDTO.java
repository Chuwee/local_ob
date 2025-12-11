package es.onebox.event.sessions.dto;

import java.io.Serializable;

public class PriceTypeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private PriceTypeAdditionalConfigDTO additionalConfig;

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

    public PriceTypeAdditionalConfigDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(PriceTypeAdditionalConfigDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

}
