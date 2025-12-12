package es.onebox.flc.security;

import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import jakarta.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FlcSecurityChecker implements SecurityChecker {

    private AuthenticationService authenticationService;
    @Value("${flc.entity.entityId}")
    private Long flcEntityId;

    @Autowired
    public FlcSecurityChecker(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean validateEntity() {
        return flcEntityId.equals(AuthenticationService.getEntityId());
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, ApiConfig.FLCApiConfig.API_CONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return ApiConfig.FLCApiConfig.CLIENT_ID.contains(clientId);
    }

    @Override
    public void authentication() {
        authenticationService.validateAuthentication();
    }

}
