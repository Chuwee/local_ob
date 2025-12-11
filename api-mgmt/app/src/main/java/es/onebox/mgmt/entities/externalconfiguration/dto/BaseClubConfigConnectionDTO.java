package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.externalconfiguration.enums.Protocol;
import es.onebox.mgmt.entities.externalconfiguration.enums.WSConnectionVersion;

import java.io.Serial;
import java.io.Serializable;

public class BaseClubConfigConnectionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1293518462577855336L;

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("port")
    private Integer port;

    @JsonProperty("protocol")
    private Protocol protocol;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("ws_connection_version")
    private WSConnectionVersion wsConnectionVersion;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public WSConnectionVersion getWsConnectionVersion() {
        return wsConnectionVersion;
    }

    public void setWsConnectionVersion(WSConnectionVersion wsConnectionVersion) {
        this.wsConnectionVersion = wsConnectionVersion;
    }
}
