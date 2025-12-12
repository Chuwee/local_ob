package es.onebox.internal.xmlsepa.security;

import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import es.onebox.internal.config.InternalApiConfig;
import jakarta.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class XMLSepaSecurityChecker implements SecurityChecker {

    private final AuthenticationService authenticationService;

    @Autowired
    public XMLSepaSecurityChecker(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean validateEntity() {
        return true;
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, InternalApiConfig.XMLSepa.SUBCONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return InternalApiConfig.XMLSepa.CLIENT_ID.contains(clientId);
    }

    @Override
    public void authentication() {
        authenticationService.validateAuthentication();
    }
}
