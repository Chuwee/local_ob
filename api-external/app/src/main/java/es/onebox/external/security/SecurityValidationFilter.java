package es.onebox.external.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.security.SecurityChecker;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.webmvc.exception.ApiErrorDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;


public class SecurityValidationFilter extends GenericFilterBean {


    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper jacksonMapper;

    @Autowired(required = false)
    private List<SecurityChecker> availableCheckers;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        boolean authenticated = authenticationService.initAuthContext();
        boolean valid = !authenticated;
        if (authenticated) {
            for (SecurityChecker availableChecker : availableCheckers) {
                if (availableChecker.check(servletRequest, AuthenticationService.getClientId())) {
                    valid = true;
                    break;
                }
            }
        }
        if (!valid) {
            sendError(httpResponse, new OneboxRestException(ApiExternalErrorCode.ACCESS_DENIED));
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
        authenticationService.finishAuthContext();
    }

    private void sendError(HttpServletResponse httpResponse, OneboxRestException e) throws IOException {
        httpResponse.setStatus(e.getHttpStatus().value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        this.jacksonMapper.writeValue(httpResponse.getOutputStream(), new ApiErrorDTO(e.getErrorCode(), e.getMessage()));
    }

}
