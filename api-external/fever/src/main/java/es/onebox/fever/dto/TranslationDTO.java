package es.onebox.fever.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class TranslationDTO {

    @JsonProperty("default_value")
    @NotNull(message = "Translation default value cannot be null")
    @NotBlank(message = "Translation default value cannot be blank")
    private String defaultValue;
    private Map<String, String> translations;

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
