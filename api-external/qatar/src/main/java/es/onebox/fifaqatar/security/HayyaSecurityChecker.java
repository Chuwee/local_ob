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
public class HayyaSecurityChecker implements SecurityChecker {

    private final AuthenticationService authenticationService;

    @Value("${hayya.entity.entityId}")
    private Long hayyaEntityId;

    @Autowired
    public HayyaSecurityChecker(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean validateEntity() {
        return hayyaEntityId.equals(AuthenticationService.getEntityId());
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, ApiConfig.HayyaApiConfig.API_CONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return ApiConfig.HayyaApiConfig.CLIENT_ID.contains(clientId);
    }

    @Override
    public void authentication() {
        authenticationService.validateAuthentication();
    }
}
