package es.onebox.mgmt.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SearchSessionsDTO extends BaseSessionDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 925422274977788848L;

    private SettingsSessionsSearchDTO settings;

    public SettingsSessionsSearchDTO getSettings() {
        return settings;
    }

    public void setSettings(SettingsSessionsSearchDTO settings) {
        this.settings = settings;
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
