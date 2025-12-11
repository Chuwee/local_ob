package es.onebox.event.catalog.dto;

import java.io.Serializable;
import java.util.Map;

public class PriceZoneDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String code;
    private String description;
    private Long priority;
    private String color;
    private Map<String, String> translatedNames;
    private Map<String, String> translatedDescriptions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Map<String, String> getTranslatedNames() {
        return translatedNames;
    }

    public void setTranslatedNames(Map<String, String> translatedNames) {
        this.translatedNames = translatedNames;
    }

    public Map<String, String> getTranslatedDescriptions() {
        return translatedDescriptions;
    }

    public void setTranslatedDescriptions(Map<String, String> translatedDescriptions) {
        this.translatedDescriptions = translatedDescriptions;
    }
}
