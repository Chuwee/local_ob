package es.onebox.fifaqatar.config.interceptor;

import es.onebox.fifaqatar.config.amt.AMTTag;
import es.onebox.fifaqatar.config.context.AppRequestContext;
import es.onebox.fifaqatar.error.InvalidAuthException;
import es.onebox.fifaqatar.adapter.datasource.FeverMeDatasource;
import es.onebox.fifaqatar.adapter.datasource.dto.MeResponseDTO;
import es.onebox.fifaqatar.tickets.service.TicketService;
import es.onebox.tracer.core.AMT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class FifaQatarSecurityInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FifaQatarSecurityInterceptor.class);

    private final FeverMeDatasource meDatasource;

    public FifaQatarSecurityInterceptor(FeverMeDatasource meDatasource) {
        this.meDatasource = meDatasource;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = extractJWT(request);
        if (StringUtils.isBlank(accessToken)) {
            throw new InvalidAuthException();
        }

        try {
            MeResponseDTO me = meDatasource.me(accessToken);
            AppRequestContext.setCurrentUser(me);

            AMT.addTracingAndAuditProperty(AMTTag.FEVER_USER_ID.value(), me.getId());
            AMT.addTracingAndAuditProperty(AMTTag.FEVER_USER_EMAIL.value(), me.getEmail());
        } catch (Exception e) {
            LOGGER.error("[FIFA QATAR] Error while getting user info: {}", e.getMessage());
            throw new InvalidAuthException();
        }

        return true;
    }

    private String extractJWT(HttpServletRequest request) {
        final String BEARER_PREFIX = "BEARER ";
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.startsWithIgnoreCase(authHeader, BEARER_PREFIX)) {
            return null;
        }

        return authHeader.substring(BEARER_PREFIX.length());
    }
}
