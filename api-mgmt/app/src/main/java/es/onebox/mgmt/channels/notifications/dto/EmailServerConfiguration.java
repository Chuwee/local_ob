package es.onebox.mgmt.channels.notifications.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.notifications.enums.EmailServerSecurityType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class EmailServerConfiguration implements Serializable {

    @Serial
    private static final long serialVersionUID = 2594299155423366937L;

    @NotNull(message = "server can not be null")
    private String server;
    @NotNull(message = "port can not be null")
    @Min(value = 0, message = "port must be grater than 0")
    private Integer port;
    @NotNull(message = "security can not be null")
    private EmailServerSecurityType security;
    private String user;
    private String password;
    @JsonProperty("require_auth")
    private Boolean requireAuth;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EmailServerSecurityType getSecurity() {
        return security;
    }

    public void setSecurity(EmailServerSecurityType security) {
        this.security = security;
    }

    public Boolean getRequireAuth() {
        return requireAuth;
    }

    public void setRequireAuth(Boolean requireAuth) {
        this.requireAuth = requireAuth;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
