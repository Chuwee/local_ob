package es.onebox.external.security;

import es.onebox.common.config.ApiConfig;
import es.onebox.oauth2.resource.AuthType;
import es.onebox.oauth2.resource.spring.boot.config.OAuth2ResourceServerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfiguration extends OAuth2ResourceServerConfiguration {

    private static final String AUTH_SCOPE = "api-external-all";
    private static final String AUTH_RESOURCE_ID = "api-external";

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2Config())
                .exceptionHandling(eh -> eh.accessDeniedHandler(accessDeniedHandler()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/info",
                                "/health",
                                "/prometheus",
                                ApiConfig.ATMApiConfig.BASE_URL + "/webhook",
                                ApiConfig.ATMApiConfig.BASE_URL + "/webhook/**",
                                ApiConfig.ATMApiConfig.BASE_URL + "/orders/*/tickets/*",
                                ApiConfig.ATMApiConfig.BASE_URL + "/orders/*/items/*/tickets/*",
                                ApiConfig.FCBApiConfig.BASE_URL + "/webhook",
                                ApiConfig.FCBApiConfig.BASE_URL + "/operation-id",
                                ApiConfig.FCBApiConfig.BASE_URL + "/operation-id/*",
                                ApiConfig.FCBApiConfig.BASE_URL + "/venue-mappings",
                                ApiConfig.FeverApiConfig.BASE_URL + "/webhook",
                                ApiConfig.ChelseaApiConfig.BASE_URL + "/webhook",
                                ApiConfig.BepassApiConfig.API_CONTEXT + "/v*/**",
                                ApiConfig.ChannelsApiConfig.API_CONTEXT + "/v*/**",
                                ApiConfig.ChannelFeedsApiConfig.API_CONTEXT + "/v*/**",
                                ApiConfig.InternalApiConfig.BASE_URL + "/sgtm/webhook",
                                "/chelsea-api/v1/fusionauth/webhook",
                                ApiConfig.QatarApiConfig.API_CONTEXT + "/**", //TODO unsecured temporary
                                ApiConfig.CustomerSyncApiConfig.BASE_URL + "/**"
                        )
                        .permitAll()
                        .requestMatchers("/**").authenticated()
                        .anyRequest()
                        .hasAnyRole(scopes().toArray(String[]::new)))
                .addFilterBefore(securityValidationFilter(), AnonymousAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public SecurityValidationFilter securityValidationFilter() {
        return new SecurityValidationFilter();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/fifa-qatar/api/**"
                );
    }


    @Override
    protected String resourceId() {
        return AUTH_RESOURCE_ID;
    }

    @Override
    protected String context() {
        return null;
    }

    @Override
    protected List<String> clientIds() {
        return ApiConfig.CLIENT_IDS;
    }

    @Override
    protected List<String> scopes() {
        return List.of(AUTH_SCOPE);
    }

    @Override
    protected AuthType kindOfAuth() {
        return AuthType.JWT;
    }
}
