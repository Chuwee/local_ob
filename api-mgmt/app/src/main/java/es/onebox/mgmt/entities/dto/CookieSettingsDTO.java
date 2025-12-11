package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CookieSettingsDTO extends CookieSettingsBaseDTO implements Serializable {
    @Serial private static final long serialVersionUID = 2L;

    @JsonProperty("history")
    private List<CookieSettingsBaseDTO> history;


    public List<CookieSettingsBaseDTO> getHistory() {
        return history;
    }
    public void setHistory(List<CookieSettingsBaseDTO> history) {
        this.history = history;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
