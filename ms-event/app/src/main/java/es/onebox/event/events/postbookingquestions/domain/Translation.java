package es.onebox.event.events.postbookingquestions.domain;

import java.util.Map;

public class Translation {

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
