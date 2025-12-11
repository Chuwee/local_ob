package es.onebox.mgmt.customdomains.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class CustomDomain implements Serializable {

    @Serial
    private static final long serialVersionUID = -8022948680219997742L;

    private String domain;
    @JsonProperty("default")
    private Boolean defaultDomain;

    public CustomDomain() {
    }

    public CustomDomain(String domain, Boolean defaultDomain) {
        this.domain = domain;
        this.defaultDomain = defaultDomain;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Boolean getDefaultDomain() {
        return defaultDomain;
    }

    public void setDefaultDomain(Boolean defaultDomain) {
        this.defaultDomain = defaultDomain;
    }
}
