package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SettingsLiveStreamingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("enable")
    private Boolean enable;

    @JsonProperty("vendor")
    private StreamingVendor vendor;

    private String value;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public StreamingVendor getVendor() {
        return vendor;
    }

    public void setVendor(StreamingVendor vendor) {
        this.vendor = vendor;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
