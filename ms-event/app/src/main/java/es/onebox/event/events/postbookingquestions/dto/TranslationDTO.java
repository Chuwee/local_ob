package es.onebox.event.events.postbookingquestions.dto;

import java.util.Map;

public class TranslationDTO {

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
