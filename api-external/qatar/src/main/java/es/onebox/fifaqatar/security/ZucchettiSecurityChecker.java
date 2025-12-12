package es.onebox.fifaqatar.security;

import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import jakarta.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZucchettiSecurityChecker implements SecurityChecker {

    private final AuthenticationService authenticationService;

    @Value("${zucchetti.entity.entityId}")
    private Long zucchettiEntityId;

    @Autowired
    public ZucchettiSecurityChecker(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean validateEntity() {
        return zucchettiEntityId.equals(AuthenticationService.getEntityId());
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, ApiConfig.ZucchettiApiConfig.API_CONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return ApiConfig.ZucchettiApiConfig.CLIENT_ID.contains(clientId);
    }

    @Override
    public void authentication() {
        authenticationService.validateAuthentication();
    }
}
