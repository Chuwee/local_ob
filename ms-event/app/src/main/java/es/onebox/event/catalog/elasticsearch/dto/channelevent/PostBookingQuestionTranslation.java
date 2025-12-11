package es.onebox.event.catalog.elasticsearch.dto.channelevent;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class PostBookingQuestionTranslation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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