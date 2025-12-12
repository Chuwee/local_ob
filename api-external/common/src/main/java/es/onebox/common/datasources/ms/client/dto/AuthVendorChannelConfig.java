package es.onebox.common.datasources.ms.client.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AuthVendorChannelConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer channelId;
    private Integer entityId;
    private AuthVendorUserDataConfig userDataConfig;
    private AuthVendorSSOConfig ssoConfig;
    private List<String> vendors;
    private List<String> ssoVendors;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public AuthVendorUserDataConfig getUserDataConfig() {
        return userDataConfig;
    }

    public void setUserDataConfig(AuthVendorUserDataConfig userDataConfig) {
        this.userDataConfig = userDataConfig;
    }

    public AuthVendorSSOConfig getSsoConfig() {
        return ssoConfig;
    }

    public void setSsoConfig(AuthVendorSSOConfig ssoConfig) {
        this.ssoConfig = ssoConfig;
    }

    public List<String> getVendors() {
        return vendors;
    }

    public void setVendors(List<String> vendors) {
        this.vendors = vendors;
    }

    public List<String> getSsoVendors() {
        return ssoVendors;
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
