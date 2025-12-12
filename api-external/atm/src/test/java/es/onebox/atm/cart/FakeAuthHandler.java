package es.onebox.atm.cart;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeAuthHandler {
    private static final String ATTRIBUTE = "attr";

    public static final String AUTH_ENTITY_ID = "entityId";
    public static final String API_KEY = "apiKey";
    public static final String AUTH_OPERATOR_ID = "operatorId";

    public static void setAuth(Long entityId, String... roles) {
        setAuth(entityId, entityId, roles);
    }

    public static void setAuth(Long entityId, Long operatorId, String... roles) {

        List<GrantedAuthority> authorities = Stream.of(roles)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "toKenValue", null, null);

        Map<String, Object> details = new HashMap<>();
        details.put(AUTH_ENTITY_ID, entityId.intValue());
        details.put(API_KEY, "apiKey");
        if (operatorId != null) {
            details.put(AUTH_OPERATOR_ID, operatorId);
        }
        Map<String, Object> of = new HashMap<>();
        of.put("authInfo", Map.of(ATTRIBUTE, "def_value"));
        DefaultOAuth2AuthenticatedPrincipal principal = new DefaultOAuth2AuthenticatedPrincipal(of, authorities);
        BearerTokenAuthentication authentication = new BearerTokenAuthentication(principal, token, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
