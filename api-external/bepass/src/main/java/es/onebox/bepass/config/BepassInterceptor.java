package es.onebox.bepass.config;

import es.onebox.bepass.auth.BepassAuthContext;
import es.onebox.bepass.common.BepassEntityConfiguration;
import es.onebox.bepass.common.BepassEntityService;
import es.onebox.bepass.exception.BepassErrorCode;
import es.onebox.bepass.instrumentation.AMTUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.tracer.core.AMT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Service
public class BepassInterceptor implements HandlerInterceptor {

    private static final String HEADER_ENTITY = "ob-entity-id";

    private final BepassEntityService bepassEntityService;

    public BepassInterceptor(BepassEntityService bepassEntityService) {
        this.bepassEntityService = bepassEntityService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Long entity = extractEntity(request);
        BepassEntityConfiguration config = this.bepassEntityService.getConfig(entity);
        AMT.addTracingAndAuditProperty(AMTUtils.ENTITY_ID,  entity);
        AMT.addTracingAndAuditProperty(AMTUtils.BEPASS_TENANT, AMTUtils.resolveTenant(config));
        BepassAuthContext.add(config);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        BepassAuthContext.remove();
    }

    private static Long extractEntity(HttpServletRequest request) {
        String entity = request.getHeader(HEADER_ENTITY);
        if (StringUtils.isEmpty(entity) || !NumberUtils.isParsable(entity)) {
            throw new OneboxRestException(BepassErrorCode.INVALID_REQUEST);
        }
        return Long.parseLong(entity);
    }
}
