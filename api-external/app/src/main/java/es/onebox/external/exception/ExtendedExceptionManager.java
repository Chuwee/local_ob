package es.onebox.external.exception;

import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.webmvc.configuration.DefaultWebConfiguration;
import es.onebox.core.webmvc.exception.ApiErrorDTO;
import es.onebox.core.webmvc.exception.ErrorDTO;
import es.onebox.datasource.http.exception.TimeoutException;
import es.onebox.fever.exception.ApiErrorRetryDTO;
import es.onebox.fever.exception.FeverException;
import es.onebox.oauth2.resource.spring.boot.exception.SecuredExceptionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ExtendedExceptionManager extends SecuredExceptionManager {

    @Autowired
    public ExtendedExceptionManager(DefaultWebConfiguration webConfiguration) {
        super(webConfiguration);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDTO> handleError(HttpServletRequest req, AccessDeniedException e) {
        return prepareResponseEntityErrorBuilder(req, e)
                .errorCode(ApiExternalErrorCode.ACCESS_DENIED)
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDTO> handleError(HttpServletRequest req, MissingServletRequestParameterException e) {
        return prepareResponseEntityErrorBuilder(req, e)
                .errorCode(ApiExternalErrorCode.BAD_REQUEST_PARAMETER)
                .build();
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorDTO> handleError(HttpServletRequest req, TimeoutException e) {
        return prepareResponseEntityErrorBuilder(req, e)
                .errorCode(ApiExternalErrorCode.TIMEOUT)
                .build();
    }

    @ExceptionHandler(FeverException.class)
    public ResponseEntity<ErrorDTO> handleError(HttpServletRequest req, FeverException e) {

        ApiErrorDTO errorDTO = new ApiErrorDTO(e.getErrorCode(), e.getMessage());
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Integer retryAfter = null;
        headers.putAll(e.getHeaders());
        if (e.getHeaders().containsKey(HttpHeaders.RETRY_AFTER)) {
            retryAfter = Integer.parseInt(e.getHeaders().get(HttpHeaders.RETRY_AFTER).get(0));
        }


        return new ResponseEntity<>(new ApiErrorRetryDTO(errorDTO, retryAfter), headers, e.getHttpStatus());
    }
}
