package es.onebox.common.datasources.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class TokenRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("client_id")
    private final String clientId;

    @JsonProperty("client_secret")
    private final String clientSecret;

    @JsonProperty("grant_type")
    private final String grantType;

    private final String username;

    private final String password;

    @JsonProperty("channel_id")
    private final Long channelId;

    @JsonProperty("channelId")
    private final Long channelIdOld;

    @JsonProperty("psw_md5")
    private final Boolean passwordInMD5;

    @JsonProperty("entity_id")
    private final Long entityId;

    private final String terminal;

    private final String terminalLicense;

    private final Integer posId;

    private TokenRequest(Builder builder) {
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.grantType = builder.grantType;
        this.username = builder.username;
        this.password = builder.password;
        this.channelId = builder.channelId;
        this.channelIdOld = builder.channelIdOld;
        this.passwordInMD5 = builder.passwordInMD5;
        this.entityId = builder.entityId;
        this.terminal = builder.terminal;
        this.terminalLicense = builder.terminalLicense;
        this.posId = builder.posId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Long getChannelId() {
        return channelId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getTerminal() {
        return terminal;
    }

    public String getTerminalLicense() {
        return terminalLicense;
    }

    public Integer getPosId() {
        return posId;
    }

    public Long getChannelIdOld() {
        return channelIdOld;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String clientId;
        private String clientSecret;
        private String grantType;
        private String username;
        private String password;
        private Long channelId;
        private Long channelIdOld;
        private Boolean passwordInMD5;
        private Long entityId;
        private String terminal;
        private String terminalLicense;
        private Integer posId;

        private Builder() {
            super();
        }

        public Builder withClientIdAndSecret(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder withGrantType(String grantType) {
            this.grantType = grantType;
            return this;
        }

        public Builder withUserPass(String username, String password, Boolean inMD5) {
            this.username = username;
            this.password = password;
            this.passwordInMD5 = inMD5;
            return this;
        }

        public Builder withChannelId(Long channelId) {
            this.channelId = channelId;
            return this;
        }
        public Builder withChannelIdOld(Long channelIdOld) {
            this.channelIdOld = channelIdOld;
            return this;
        }

        public Builder withEntityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder withTerminal(String terminal, String terminalLicense) {
            this.terminal = terminal;
            this.terminalLicense = terminalLicense;
            return this;
        }

        public Builder withPointOfSale(Integer posId) {
            this.posId = posId;
            return this;
        }

        public TokenRequest build() {
            return new TokenRequest(this);
        }
    }
}