package es.onebox.mgmt.datasources.ms.channel.dto.authvendor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelAuthVendor implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> vendors;
    private ChannelAuthVendorUserData userDataConfig;
    private ChannelAuthVendorSso ssoConfig;
    private List<String> ssoVendors;

    public List<String> getVendors() {
        return this.vendors;
    }

    public void setVendors(List<String> vendors) {
        this.vendors = vendors;
    }

    public ChannelAuthVendorUserData getUserDataConfig() {
        return this.userDataConfig;
    }

    public void setUserDataConfig(ChannelAuthVendorUserData userDataConfig) {
        this.userDataConfig = userDataConfig;
    }

    public ChannelAuthVendorSso getSsoConfig() {
        return this.ssoConfig;
    }

    public void setSsoConfig(ChannelAuthVendorSso ssoConfig) {
        this.ssoConfig = ssoConfig;
    }

    public List<String> getSsoVendors() {
        return this.ssoVendors;
    }

    public void setSsoVendors(List<String> ssoVendors) {
        this.ssoVendors = ssoVendors;
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
