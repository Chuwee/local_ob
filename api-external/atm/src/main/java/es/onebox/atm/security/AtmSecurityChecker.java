package es.onebox.atm.security;

import es.onebox.atm.config.ATMEntityConfiguration;
import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import jakarta.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AtmSecurityChecker implements SecurityChecker {

    private final AuthenticationService authenticationService;
    private final ATMEntityConfiguration atmEntityConfiguration;

    @Autowired
    public AtmSecurityChecker(AuthenticationService authenticationService, ATMEntityConfiguration atmEntityConfiguration){
        this.authenticationService = authenticationService;
        this.atmEntityConfiguration = atmEntityConfiguration;
    }

    @Override
    public boolean validateEntity() {
        return AuthenticationService.isOperatorEntityType() ||
                atmEntityConfiguration.getAllowedEntities().contains(AuthenticationService.getEntityId());
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, ApiConfig.ATMApiConfig.API_CONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return ApiConfig.ATMApiConfig.CLIENT_ID.contains(clientId);
    }

    @Override
    public void authentication() {
        authenticationService.validateAuthentication();
    }
}
