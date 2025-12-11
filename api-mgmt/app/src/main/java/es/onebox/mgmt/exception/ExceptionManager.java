package es.onebox.mgmt.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.hazelcast.core.HazelcastException;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.datasource.http.exception.TimeoutException;
import es.onebox.mgmt.common.URLExtractorUtils;
import es.onebox.oauth2.resource.spring.boot.exception.ApiAuthErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@ControllerAdvice
public class ExceptionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionManager.class);

    private static final List<ErrorCode> ERROR_CODES = new ArrayList<>();

    static {
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtTierErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtSessionErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtPassbookErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtVenueErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtPromotionErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtChannelsErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtCollectivesErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtSeasonTicketErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtAccessControlErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtInsuranceErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtEntitiesErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtDeliveryErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtExportsErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtCustomersErrorCode.values()));
        ERROR_CODES.addAll(Arrays.asList(ApiMgmtExternalEventErrorCode.values()));
    }

    @ExceptionHandler(OneboxRestException.class)
    public ResponseEntity<ApiErrorDTO> errorHandler(HttpServletRequest req, OneboxRestException e) {
        ErrorCode errorCode = ERROR_CODES.stream()
                .filter(ec -> ec.getErrorCode().equals(e.getErrorCode()))
                .findFirst()
                .orElse(ApiMgmtErrorCode.GENERIC_ERROR);
        return prepareResponseError(req, e, errorCode);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorDTO> errorHandler(HttpServletRequest req, BindException bindingResult) {
        FieldError fieldError = bindingResult.getFieldError();
        ErrorCode errorCode = ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;
        if (fieldError == null) {
            return prepareResponseError(req, bindingResult, errorCode);
        } else {
            traceError(req, bindingResult, errorCode);
            String message = String.format("Parameter %s: %s", fieldError.getField(), fieldError.getDefaultMessage());
            ApiErrorDTO apiErrorDTO = new ApiErrorDTO(errorCode, message);
            return new ResponseEntity<>(apiErrorDTO, errorCode.getHttpStatus());
        }

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDTO> errorHandler(HttpServletRequest req, HttpMessageNotReadableException e) {
        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) cause;
            List<JsonMappingException.Reference> paths = invalidFormatException.getPath();
            JsonMappingException.Reference path = paths.get(paths.size() - 1);
            String message = ApiMgmtErrorCode.INVALID_PARAM_FORMAT.formatMessage(path.getFieldName(), invalidFormatException.getValue());
            return prepareResponseError(req, e, ApiMgmtErrorCode.INVALID_PARAM_FORMAT, message);
        } else if (cause instanceof JsonMappingException) {
            return prepareResponseError(req, e, ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, ApiMgmtErrorCode.BAD_REQUEST_PARAMETER.getMessage());
        }else if(e.getMessage() != null && e.getMessage().contains("springframework")) {
            return prepareResponseError(req, e, ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, ApiMgmtErrorCode.BAD_REQUEST_PARAMETER.getMessage());
        }
        return prepareResponseError(req, e, ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> errorHandler(HttpServletRequest req, Exception e) {
        return prepareResponseError(req, e, ApiMgmtErrorCode.GENERIC_ERROR);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiErrorDTO> handleError(HttpServletRequest req,
                                                   MethodArgumentNotValidException e) {
        ObjectError error = null;
        if (e.getBindingResult().hasFieldErrors()) {
            error = e.getBindingResult().getFieldError();
        }
        if (e.getBindingResult().hasGlobalErrors()) {
            error = e.getBindingResult().getGlobalError();
        }
        Exception exception = new Exception(error != null ? error.getDefaultMessage() : ApiMgmtErrorCode.BAD_REQUEST_PARAMETER.getMessage());
        return prepareResponseError(req, exception, ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDTO> errorHandler(HttpServletRequest req, AccessDeniedException e) {
        return prepareResponseError(req, e, ApiMgmtErrorCode.FORBIDDEN_RESOURCE);
    }
    
    @ExceptionHandler({InvalidBearerTokenException.class})
    public ResponseEntity<ApiAuthErrorDTO> handleAuthenticationException(InvalidBearerTokenException e) {
        ApiAuthErrorDTO body = authenticationError(e);
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ApiAuthErrorDTO> handleAuthenticationException(AuthenticationException e) {
        ApiAuthErrorDTO body = authenticationError(e);
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(HazelcastException.class)
    public ResponseEntity<ApiErrorDTO> errorHandler(HttpServletRequest req, HazelcastException e) {
        if (e.getCause() instanceof OneboxRestException) {
            OneboxRestException cause = (OneboxRestException) e.getCause();
            return errorHandler(req, cause);
        }
        return prepareResponseError(req, e, ApiMgmtErrorCode.GENERIC_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleError(HttpServletRequest req, ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        String message = constraintViolations.iterator().next().getMessage();
        ApiErrorDTO error = new ApiErrorDTO(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, message);
        return new ResponseEntity<>(error, ApiMgmtErrorCode.BAD_REQUEST_PARAMETER.getHttpStatus());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorDTO> handleError(HttpServletRequest req, ValidationException e) {
        ApiErrorDTO error;
        if (e.getCause() instanceof OneboxRestException) {
            OneboxRestException oneboxRestException = (OneboxRestException) e.getCause();
            ErrorCode errorCode = ApiMgmtErrorCode.getByCode(oneboxRestException.getErrorCode());
            error = new ApiErrorDTO(errorCode, oneboxRestException.getMessage());
        } else {
            error = new ApiErrorDTO(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, e.getMessage());
        }

        return new ResponseEntity<>(error, ApiMgmtErrorCode.BAD_REQUEST_PARAMETER.getHttpStatus());
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ApiErrorDTO> handleError(HttpServletRequest req, TypeMismatchException e) {
        String message = String.format("Value '%s' for parameter is not correct.", e.getValue().toString());
        if (e.getRequiredType().isEnum() && e.getRequiredType().getEnumConstants() != null && e.getRequiredType().getEnumConstants().length > 0) {
            message = typeMismatchEnumCreateMessage(e);
        }else if (e.getCause() instanceof NumberFormatException) {
            message = String.format("Value '%s' is not correct, a numeral value is required for the field.", e.getValue().toString());
        }
        return prepareResponseError(req, e, ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, message);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiErrorDTO> handleError(HttpServletRequest req, TimeoutException e) {
        return prepareResponseError(req, e, ApiMgmtErrorCode.TIMEOUT, ApiMgmtErrorCode.TIMEOUT.getMessage());
    }

    private static String typeMismatchEnumCreateMessage(TypeMismatchException e) {
        String enumValues = Arrays.stream(e.getRequiredType().getEnumConstants())
                .filter(Enum.class::isInstance)
                .map(Enum.class::cast)
                .map(Enum::name)
                .collect(Collectors.joining(","));
        return String.format("Value '%s' is not correct for parameter. Possible values: '%s'.", e.getValue().toString(), enumValues);
    }

    private static ResponseEntity<ApiErrorDTO> prepareResponseError(HttpServletRequest req, Exception e, ErrorCode errorCode) {
        traceError(req, e, errorCode);
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(errorCode, e.getMessage());
        return new ResponseEntity<>(apiErrorDTO, errorCode.getHttpStatus());
    }

    private static ResponseEntity<ApiErrorDTO> prepareResponseError(HttpServletRequest req, Exception e, ErrorCode errorCode, String message) {
        traceError(req, e, errorCode);
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(errorCode, message);
        return new ResponseEntity<>(apiErrorDTO, errorCode.getHttpStatus());
    }
    
    private static ApiAuthErrorDTO authenticationError(AuthenticationException e) {
        return new ApiAuthErrorDTO("unauthorized", e.getMessage());
    }

    private static void traceError(HttpServletRequest req, Exception e, ErrorCode errorCode) {
        String error = String.format("Error in call: %s", URLExtractorUtils.getFullURI(req));
        if (LOGGER.isDebugEnabled() || ApiMgmtErrorCode.GENERIC_ERROR.equals(errorCode)) {
            LOGGER.error(error, e);
        } else {
            LOGGER.error("{} with msg: {}", error, e.getMessage());
        }
    }
}
