package es.onebox.circuitcat.security;

import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import jakarta.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CircuitSecurityChecker implements SecurityChecker {

    private final AuthenticationService authenticationService;

    @Value("${circuit-cat.entity.entityId}")
    private Long circuitEntityId;

    @Autowired
    public CircuitSecurityChecker(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean validateEntity() {
        return circuitEntityId.equals(AuthenticationService.getEntityId());
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, ApiConfig.CircuitApiConfig.API_CONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return ApiConfig.CircuitApiConfig.CLIENT_ID.contains(clientId);
    }

    @Override
    public void authentication() {
        authenticationService.validateAuthentication();
    }
}
