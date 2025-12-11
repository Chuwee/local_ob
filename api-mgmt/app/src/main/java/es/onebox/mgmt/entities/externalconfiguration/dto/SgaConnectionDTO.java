package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;

public class SgaConnectionDTO extends ConnectionBaseDTO {

    @Serial
    private static final long serialVersionUID = -6513073917019243216L;

    @JsonProperty("auth_url")
    private String authUrl;
    @JsonProperty("client_id")
    private String clientId;
    private String profile;
    private String scope;
    @JsonProperty("sales_channel_id")
    private String salesChannelId;

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSalesChannelId() {
        return salesChannelId;
    }

    public void setSalesChannelId(String salesChannelId) {
        this.salesChannelId = salesChannelId;
    }
}
