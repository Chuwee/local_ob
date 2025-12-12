package es.onebox.palisis.security;

import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.SecurityChecker;
import es.onebox.common.utils.Utilities;
import jakarta.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PalisisSecurityChecker implements SecurityChecker {

    private final AuthenticationService authenticationService;

    @Value("${palisis.entity.entityId}")
    private Long palisisEntityId;

    @Autowired
    public PalisisSecurityChecker(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean validateEntity() {
        return palisisEntityId.equals(AuthenticationService.getEntityId());
    }

    @Override
    public boolean validateContext(ServletRequest servletRequest) {
        return Utilities.checkUrlContextPath(servletRequest, ApiConfig.PalisisApiConfig.API_CONTEXT);
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
