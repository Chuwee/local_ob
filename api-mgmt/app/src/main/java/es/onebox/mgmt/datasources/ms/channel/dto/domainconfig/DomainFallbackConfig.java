package es.onebox.mgmt.datasources.ms.channel.dto.domainconfig;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class DomainFallbackConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 4528176245818588635L;

    private Boolean enabled;
    private DomainFallbackConfigMode channelsAllowedMode;
    private List<DomainFallbackConfigRule> rules;
    private String defaultRedirectionUrl;

    public DomainFallbackConfig() {
    }

    public DomainFallbackConfigMode getChannelsAllowedMode() {
        return channelsAllowedMode;
    }

    public void setChannelsAllowedMode(DomainFallbackConfigMode channelsAllowedMode) {
        this.channelsAllowedMode = channelsAllowedMode;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<DomainFallbackConfigRule> getRules() {
        return rules;
    }

    public void setRules(List<DomainFallbackConfigRule> rules) {
        this.rules = rules;
    }

    public String getDefaultRedirectionUrl() {
        return defaultRedirectionUrl;
    }

    public void setDefaultRedirectionUrl(String defaultRedirectionUrl) {
        this.defaultRedirectionUrl = defaultRedirectionUrl;
    }
}
