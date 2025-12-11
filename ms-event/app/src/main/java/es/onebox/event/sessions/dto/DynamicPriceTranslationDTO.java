package es.onebox.event.sessions.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class DynamicPriceTranslationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7028341866221092118L;

    private String language;
    @NotNull(message = "value must not be null")
    private String value;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
