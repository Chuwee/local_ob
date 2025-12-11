package es.onebox.mgmt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.Role;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityStatus;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.UsersRepository;
import es.onebox.mgmt.exception.ApiErrorDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SecurityValidationFilter extends GenericFilterBean {

    private static final String ERROR_MESSAGE = "User or password are incorrect";

    private static final Logger LOG = LoggerFactory.getLogger(SecurityValidationFilter.class);

    private final UsersRepository usersRepository;
    private final EntitiesRepository entitiesRepository;
    private final ObjectMapper jacksonMapper;

    @Autowired
    public SecurityValidationFilter(UsersRepository usersRepository, EntitiesRepository entitiesRepository, ObjectMapper jacksonMapper) {
        this.usersRepository = usersRepository;
        this.entitiesRepository = entitiesRepository;
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        BearerTokenAuthentication userAuth = SecurityUtils.getUserAuth();
        if (userAuth != null && httpRequest.getServletPath().contains(ApiConfig.API_CONTEXT)) {
            String username = SecurityUtils.getUsername();
            String password = SecurityUtils.getPassword();
            String apiKey = SecurityUtils.getApiKey();
            long operatorId = SecurityUtils.getUserOperatorId();
            long entityId = SecurityUtils.getUserEntityId();
            try {

                User user = apiKey == null ? validateUser(username, password, entityId) : validateUser(apiKey, entityId);
                validateEntity(user, entityId);
                validateOperator(user, entityId, operatorId);
                final Map<String, Object> details = SecurityUtils.getAuthDetails();
                details.put(SecurityUtils.AUTH_OPERATOR_ID, user.getOperatorId());
                details.put(SecurityUtils.AUTH_USER_ID, user.getId());

                checkRoles(userAuth, username, entityId, user);
                chain.doFilter(request, response);
            } catch (OneboxRestException e) {
                sendError(httpResponse, ApiMgmtErrorCode.getByCode(e.getErrorCode()), e.getMessage());
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    //Check if roles has changed on DB since token generation and regenerate auth if changed
    private static void checkRoles(BearerTokenAuthentication userAuth, String username, long entityId, User user) {
        List<String> authRoles = userAuth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        List<String> userRoles = user.getRoles().stream().map(Role::getCode).collect(Collectors.toList());
        if (authRoles.size() != userRoles.size() || !authRoles.containsAll(userRoles)) {
            LOG.warn("[AUTH] Outdated roles for user {} of entity {}", username, entityId);
            rebuildAuth(user);
        }
    }

    private void validateOperator(User user, Long entityId, Long operatorId) {
        if (!entityId.equals(operatorId)) {
            validateEntity(operatorId, ApiMgmtErrorCode.AUTH_OPERATOR_NOT_FOUND, ApiMgmtErrorCode.AUTH_OPERATOR_INACTIVE);
        }
        if (!operatorId.equals(user.getOperatorId())) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.UNAUTHORIZED_ACCESS, "Invalid token");
        }
    }

    private void validateEntity(Long entityId, ErrorCode notFound, ErrorCode inactive) {
        Entity entity = entitiesRepository.getEntity(entityId);
        if (entity == null) {
            throw new OneboxRestException(notFound);
        }
        if (EntityStatus.ACTIVE != entity.getState()) {
            throw new OneboxRestException(inactive);
        }
    }

    private void validateEntity(User user, Long entityId) {
        validateEntity(entityId, ApiMgmtErrorCode.AUTH_ENTITY_NOT_FOUND, ApiMgmtErrorCode.AUTH_ENTITY_INACTIVE);
        if (!entityId.equals(user.getEntityId())) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.UNAUTHORIZED_ACCESS, "Invalid token");
        }
    }

    private User validateUser(String username, String password, Long entityId) {
        User user = null;
        try {
            user = usersRepository.getAuthUserByUserName(username, entityId);
        } catch (OneboxRestException e) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.UNAUTHORIZED_ACCESS, ERROR_MESSAGE);
        }
        if (user == null || StringUtils.isBlank(password) || StringUtils.isBlank(user.getPassword()) || !password.equals(user.getPassword())) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.UNAUTHORIZED_ACCESS, ERROR_MESSAGE);
        }
        return user;
    }

    private User validateUser(String apikey, Long entityId) {
        User user = null;
        try {
            user = usersRepository.getAuthUserByApiKey(apikey, entityId);
        } catch (OneboxRestException e) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.UNAUTHORIZED_ACCESS, ERROR_MESSAGE);
        }
        if (user == null) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.UNAUTHORIZED_ACCESS, ERROR_MESSAGE);
        }
        return user;
    }

    private static void rebuildAuth(User user) {
        List<GrantedAuthority> userAuthorities = user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getCode())).collect(Collectors.toList());
        BearerTokenAuthentication in = (BearerTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        BearerTokenAuthentication out = new BearerTokenAuthentication((OAuth2AuthenticatedPrincipal) in.getPrincipal(), (OAuth2AccessToken) in.getCredentials(), userAuthorities);
        SecurityContextHolder.getContext().setAuthentication(out);
    }

    private void sendError(HttpServletResponse httpResponse, ErrorCode errorCode, String message) throws IOException {
        httpResponse.setStatus(errorCode.getHttpStatus().value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        jacksonMapper.writeValue(httpResponse.getOutputStream(), new ApiErrorDTO(errorCode, message));
    }
}
