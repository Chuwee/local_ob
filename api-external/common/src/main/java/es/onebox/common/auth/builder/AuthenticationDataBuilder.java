package es.onebox.common.auth.builder;


import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class AuthenticationDataBuilder {

    private static final String AUTH_INFO_CHANNEL_ID = "channelId";
    private static final String AUTH_INFO_TERMINAL = "terminal";
    private static final String AUTH_INFO_TERMINAL_LICENSE = "terminalLicense";
    private static final String AUTH_INFO_USER_PASS = "userPassword";
    private static final String AUTH_INFO_API_KEY = "apiKey";
    private static final String AUTH_INFO_POS_ID = "posId";
    private static final String AUTH_INFO_ENTITY_ID = "entityId";
    private static final String AUTH_INFO_OPERATOR_ID = "operatorId";

    private String username;

    private Collection<? extends GrantedAuthority> authorities;
    private String clientId;

    public AuthenticationDataBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public AuthenticationDataBuilder withAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
        return this;
    }

    public AuthenticationDataBuilder withClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public AuthenticationData build() {
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setUsername(username);
        authenticationData.setAuthorities(new ArrayList<>(authorities));
        authenticationData.setPassword(getStringAttr(AUTH_INFO_USER_PASS));
        authenticationData.setApiKey(getStringAttr(AUTH_INFO_API_KEY));
        authenticationData.setChannelId(getLongAttr(AUTH_INFO_CHANNEL_ID));
        authenticationData.setTerminal(getStringAttr(AUTH_INFO_TERMINAL));
        authenticationData.setTerminalLicense(getStringAttr(AUTH_INFO_TERMINAL_LICENSE));
        authenticationData.setPosId(getLongAttr(AUTH_INFO_POS_ID));
        authenticationData.setEntityId(getLongAttr(AUTH_INFO_ENTITY_ID));
        authenticationData.setOperatorId(getLongAttr(AUTH_INFO_OPERATOR_ID));
        authenticationData.setClientId(clientId);

        return authenticationData;
    }

    private String getStringAttr(String attributeName) {
        return (String) AuthContextUtils.getAttribute(attributeName);
    }

    private Long getLongAttr(String attributeName) {
        Integer value = (Integer) AuthContextUtils.getAttribute(attributeName);
        return value == null ? null : value.longValue();
    }
}
