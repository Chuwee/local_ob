package es.onebox.eci.security;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import jakarta.servlet.ServletRequest;
import org.springframework.stereotype.Component;

@Component
public class EciSecurityChecker implements SecurityChecker {
    @Override
    public boolean validateEntity() {
        return true;
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, ApiConfig.ECIApiConfig.API_CONTEXT);
    }

    @Override
    public boolean validateClientID(String clientId) {
        return ApiConfig.ECIApiConfig.CLIENT_ID.equals(clientId);
    }

    @Override
    public void authentication() {

    }
}
