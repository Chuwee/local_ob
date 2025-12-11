package es.onebox.mgmt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.oauth2.resource.AuthType;
import es.onebox.oauth2.resource.spring.boot.config.OAuth2ResourceServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.filter.GenericFilterBean;

import java.util.List;

@Configuration
public class SecurityConfiguration extends OAuth2ResourceServerConfiguration {


    private final SecurityValidationFilter securityValidationFilter;
    private final ObjectMapper jacksonMapper;

    @Autowired
    public SecurityConfiguration(ObjectMapper jacksonMapper, SecurityValidationFilter securityValidationFilter) {
        this.jacksonMapper = jacksonMapper;
        this.securityValidationFilter = securityValidationFilter;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2Config())
                .exceptionHandling(cfg -> cfg.accessDeniedHandler(customAccessDeniedHandler(jacksonMapper)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(defaultAnonymousPaths())
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/mgmt-api/v1/users/{\\d+}/password")).permitAll()
                        .requestMatchers(HttpMethod.GET, "/mgmt-api/v1/users/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/mgmt-api/v1/users/forgot-password", "/mgmt-api/v1/users/forgot-password/recover").permitAll()
                        .requestMatchers(context() + "/**")
                        .authenticated()
                        .anyRequest()
                        .hasAnyRole(scopes().toArray(String[]::new)))
                .addFilterBefore(securityValidationFilter, AnonymousAuthenticationFilter.class);

        http.headers(headers -> headers
                // X-Permitted-Cross-Domain-Policies all endpoints
                .addHeaderWriter(new StaticHeadersWriter("X-Permitted-Cross-Domain-Policies", "none"))
                // Clear-Site-Data only in forgot-password
                .addHeaderWriter(new DelegatingRequestMatcherHeaderWriter(
                        new AntPathRequestMatcher(ApiConfig.BASE_URL + "/users/forgot-password", "POST"),
                        new StaticHeadersWriter("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\"")
                )));

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<GenericFilterBean> registration() {
        var registration = new FilterRegistrationBean<GenericFilterBean>(securityValidationFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Override
    protected String resourceId() {
        return ApiConfig.AUTH_RESOURCE_ID;
    }

    @Override
    protected String context() {
        return ApiConfig.API_CONTEXT;
    }

    @Override
    protected List<String> scopes() {
        return List.of(ApiConfig.AUTH_SCOPE);
    }

    @Override
    protected AuthType kindOfAuth() {
        return AuthType.JWT;
    }

    @Override
    protected List<String> clientIds() {
        return List.of(ApiConfig.ONEBOX_CLIENT_ID);
    }

    private CustomAccessDeniedHandler customAccessDeniedHandler(ObjectMapper jacksonMapper) {
        return new CustomAccessDeniedHandler(jacksonMapper);
    }
}
