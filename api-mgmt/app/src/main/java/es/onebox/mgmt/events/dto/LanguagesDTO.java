package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class LanguagesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("default")
    private String defaultLanguage;

    @JsonProperty("selected")
    private List<String> selected;

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public List<String> getSelected() {
        return selected;
    }

    public void setSelected(List<String> selected) {
        this.selected = selected;
    }
}
