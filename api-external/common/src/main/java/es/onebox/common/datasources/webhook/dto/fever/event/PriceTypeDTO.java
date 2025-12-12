package es.onebox.common.datasources.webhook.dto.fever.event;


import java.io.Serializable;

public class PriceTypeDTO implements Serializable {

    private Long id;
    private String name;
    private String color;
    private Boolean isDefault;
    private String code;
    private Long priority;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public PriceTypeAdditionalConfigDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(PriceTypeAdditionalConfigDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
    }
}
