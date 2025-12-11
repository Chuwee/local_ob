package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;
import java.util.Map;

public class TranlationDTO {

    @JsonProperty("default_value")
    private String defaultValue;
    private Map<String, String> translations;

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }
}
