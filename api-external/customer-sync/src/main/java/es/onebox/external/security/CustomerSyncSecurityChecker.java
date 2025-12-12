package es.onebox.external.security;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import jakarta.servlet.ServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomerSyncSecurityChecker implements SecurityChecker {

    @Override
    public boolean validateEntity() {
        return true;
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, ApiConfig.CustomerSyncApiConfig.API_CONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return true;
    }

    @Override
    public void authentication() { }
}
