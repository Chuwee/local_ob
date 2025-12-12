package es.onebox.internal.automaticrenewals.security;

import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import es.onebox.internal.config.InternalApiConfig;
import jakarta.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutomaticRenewalsSecurityChecker implements SecurityChecker {

    private final AuthenticationService authenticationService;

    @Autowired
    public AutomaticRenewalsSecurityChecker(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean validateEntity() {
        return true;
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, InternalApiConfig.AutomaticRenewals.SUBCONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return InternalApiConfig.AutomaticRenewals.CLIENT_ID.contains(clientId);
    }

    @Override
    public void authentication() {
        authenticationService.validateAuthentication();
    }
}
