package es.onebox.mgmt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.webmvc.exception.ApiErrorDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    public CustomAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException, ServletException {
        ApiErrorDTO error = new ApiErrorDTO(ApiMgmtErrorCode.FORBIDDEN_RESOURCE.getErrorCode(), ex.getMessage());
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getOutputStream().write(this.mapper.writeValueAsBytes(error));
    }
}
