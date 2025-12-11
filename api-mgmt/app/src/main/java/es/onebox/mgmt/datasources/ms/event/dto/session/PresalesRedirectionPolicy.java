package es.onebox.mgmt.datasources.ms.event.dto.session;


import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class PresalesRedirectionPolicy implements Serializable {
    @Serial
    private static final long serialVersionUID = 3827169283746192734L;

    @NotNull(message = "mode can not be null")
    private PresalesLinkMode mode;

    private Map<String, String> value;

    public PresalesLinkMode getMode() {
        return mode;
    }

    public void setMode(PresalesLinkMode mode) {
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
