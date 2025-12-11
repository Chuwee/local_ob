package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.sessions.enums.SessionVirtualQueueVersion;
import jakarta.validation.constraints.Pattern;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SessionVirtualQueueDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2160608732535305261L;

    private Boolean enable;

    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "settings.virtual_queue.alias must contain only alphanumeric characters")
    private String alias;

    @JsonProperty("skip_token")
    private String skipQueueToken;

    private SessionVirtualQueueVersion version;


    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSkipQueueToken() {
        return skipQueueToken;
    }

    public void setSkipQueueToken(String skipQueueToken) {
        this.skipQueueToken = skipQueueToken;
    }

    public SessionVirtualQueueVersion getVersion() {
        return version;
    }

    public void setVersion(SessionVirtualQueueVersion version) {
        this.version = version;
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
