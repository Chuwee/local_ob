package es.onebox.internal.security;

import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import jakarta.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InternalSecurityChecker implements SecurityChecker {

    private final AuthenticationService authenticationService;

    @Autowired
    public InternalSecurityChecker(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean validateEntity() {
        return true;
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, ApiConfig.InternalApiConfig.API_CONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return true;
    }

    @Override
    public void authentication() {
        authenticationService.validateAuthentication();
    }
}
