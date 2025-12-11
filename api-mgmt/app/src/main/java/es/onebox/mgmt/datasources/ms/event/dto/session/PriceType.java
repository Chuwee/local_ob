package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.io.Serializable;

public class PriceType implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private PriceTypeAdditionalConfig additionalConfig;

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

    public PriceTypeAdditionalConfig getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(PriceTypeAdditionalConfig additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

}
