package es.onebox.event.sessions.domain.sessionconfig;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class PresalesRedirectionPolicy implements Serializable {

    @Serial
    private static final long serialVersionUID = 5629387491023456789L;

    @NotNull(message = "mode can not be null")
    private PresalesRedirectionLinkMode mode;

    private Map<String, String> value;

    public PresalesRedirectionLinkMode getMode() {
        return mode;
    }

    public void setMode(PresalesRedirectionLinkMode mode) {
        this.mode = mode;
    }

    public Map<String, String> getValue() {
        return value;
    }

    public void setValue(Map<String, String> value) {
        this.value = value;
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
