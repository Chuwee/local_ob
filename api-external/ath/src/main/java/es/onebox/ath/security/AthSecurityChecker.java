package es.onebox.ath.security;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletRequest;

@Component
public class AthSecurityChecker implements SecurityChecker {

    private final AuthenticationService authenticationService;

    @Value("${ath.entity.entityId}")
    private Long athEntityId;

    @Autowired
    public AthSecurityChecker(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean validateEntity() {
        return AuthenticationService.isOperatorEntityType() || athEntityId.equals(AuthenticationService.getEntityId());
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, ApiConfig.ATHApiConfig.API_CONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return ApiConfig.ATHApiConfig.CLIENT_ID.contains(clientId);
    }

    @Override
    public void authentication() {
        authenticationService.validateAuthentication();
    }
}
