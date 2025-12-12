package es.onebox.fever.security;

import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import jakarta.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeverSecurityChecker implements SecurityChecker {

    private AuthenticationService authenticationService;


    @Autowired
    public FeverSecurityChecker(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean validateEntity() {
        return true;
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
         return Utilities.checkUrlContextPath(servletRequest, ApiConfig.FeverApiConfig.API_CONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return ApiConfig.FeverApiConfig.CLIENT_ID.contains(clientId);
    }


    @Override
    public void authentication() {
        authenticationService.validateAuthentication();
    }

}
