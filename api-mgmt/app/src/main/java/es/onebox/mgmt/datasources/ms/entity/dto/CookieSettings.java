package es.onebox.mgmt.datasources.ms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CookieSettings extends CookieSettingsBase implements Serializable {
    @Serial private static final long serialVersionUID = 2L;

    @JsonProperty("history")
    private List<CookieSettingsBase> history;


    public List<CookieSettingsBase> getHistory() {
        return history;
    }
    public void setHistory(List<CookieSettingsBase> history) {
        this.history = history;
    }
}
