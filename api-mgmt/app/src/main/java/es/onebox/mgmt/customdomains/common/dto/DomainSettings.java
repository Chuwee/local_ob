package es.onebox.mgmt.customdomains.common.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class DomainSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = 4844021320182281564L;

    private Boolean enabled;
    private DomainMode mode;
    private List<CustomDomain> domains;

    public DomainSettings() {
    }

    public DomainSettings(Boolean enabled, DomainMode mode, List<CustomDomain> domains) {
        this.enabled = enabled;
        this.mode = mode;
        this.domains = domains;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public DomainMode getMode() {
        return mode;
    }

    public void setMode(DomainMode mode) {
        this.mode = mode;
    }

    public List<CustomDomain> getDomains() {
        return domains;
    }

    public void setDomains(List<CustomDomain> domains) {
        this.domains = domains;
    }
}
