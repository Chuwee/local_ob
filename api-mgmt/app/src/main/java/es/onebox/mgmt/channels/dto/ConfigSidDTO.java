package es.onebox.mgmt.channels.dto;

import java.io.Serializable;

public class ConfigSidDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String configSid;

    public ConfigSidDTO() {
    }

    public ConfigSidDTO(String configSid) {
        this.configSid = configSid;
    }

    public String getConfigSid() {
        return configSid;
    }

    public void setConfigSid(String configSid) {
        this.configSid = configSid;
    }
}
