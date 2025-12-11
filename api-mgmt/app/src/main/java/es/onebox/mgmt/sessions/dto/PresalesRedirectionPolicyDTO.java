package es.onebox.mgmt.sessions.dto;

import es.onebox.mgmt.sessions.enums.PresalesLinkDestinationMode;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class PresalesRedirectionPolicyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7852193012391234567L;

    @NotNull(message = "mode can not be null")
    private PresalesLinkDestinationMode mode;

    private Map<String, String> value;

    public PresalesLinkDestinationMode getMode() {
        return mode;
    }

    public void setMode(PresalesLinkDestinationMode mode) {
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
