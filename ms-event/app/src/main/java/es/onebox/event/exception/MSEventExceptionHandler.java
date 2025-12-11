package es.onebox.event.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestError;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

@ControllerAdvice
public class MSEventExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MSEventExceptionHandler.class);

    @ExceptionHandler(OneboxRestException.class)
    @ResponseBody
    public ResponseEntity<OneboxRestError> handleOneboxRestException(OneboxRestException e) {
        return handleError(e.getErrorCode(),
                e.getMessage(),
                e.getDeveloperMessage(),
                e.getHttpStatus(),
                e,
                false);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<OneboxRestError> errorHandler(HttpMediaTypeNotSupportedException e) {
        return handleError(null, CoreErrorCode.CONTENT_TYPE_MUST_BE_JSON, e);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseBody
    public ResponseEntity<OneboxRestError> errorHandler(HandlerMethodValidationException e) {
        String detail = null;
        if (CollectionUtils.isNotEmpty(e.getAllValidationResults())) {
            detail = " - " + e.getAllValidationResults().get(0).toString();
        }
        CoreErrorCode errorCode = CoreErrorCode.BAD_PARAMETER;
        return handleError(errorCode.getErrorCode(), e.getMessage(), detail, errorCode.getHttpStatus(), e, false);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<OneboxRestError> errorHandler(HttpMessageNotReadableException e) {
        String message = null;
        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            List<JsonMappingException.Reference> paths = invalidFormatException.getPath();
            JsonMappingException.Reference path = paths.get(paths.size() - 1);
            message = String.format("Invalid '%s' format: '%s'", path.getFieldName(), invalidFormatException.getValue());
        }
        return handleError(message, CoreErrorCode.BAD_PARAMETER, e);
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<OneboxRestError> errorHandler(BindException e) {
        return handleBindingResult(e, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<OneboxRestError> errorHandler(MethodArgumentNotValidException e) {
        return handleBindingResult(e.getBindingResult(), e);
    }

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseBody
	public ResponseEntity<OneboxRestError> errorHandler(EntityNotFoundException e) {
		CoreErrorCode errorCode = CoreErrorCode.NOT_FOUND;
		return handleError(errorCode.getErrorCode(), e.getMessage(), e.getLocalizedMessage(),
				errorCode.getHttpStatus(),
				e, false);
	}


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<OneboxRestError> errorHandler(Exception e) {
        return handleError(null, CoreErrorCode.GENERIC_ERROR, e);
    }

    private ResponseEntity<OneboxRestError> handleBindingResult(BindingResult result, Exception e) {
        String message;
        FieldError fieldError = result.getFieldError();
        if (fieldError == null) {
            message = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .findFirst().orElse(result.toString());
        } else {
            message = fieldError.getField() + ": " + fieldError.getDefaultMessage();
        }
        return handleError(message, CoreErrorCode.BAD_PARAMETER, e);
    }

    private ResponseEntity<OneboxRestError> handleError(String message, ErrorCode errorCode, Exception e) {
        String msg = message == null ? errorCode.getMessage() : message;
        HttpStatus httpStatus = errorCode.getHttpStatus();
        String code = errorCode.getErrorCode();
        return handleError(code, msg, null, httpStatus, e, true);
    }

    private ResponseEntity<OneboxRestError> handleError(String code,
                                                        String message,
                                                        String developerMessage,
                                                        HttpStatus httpStatus,
                                                        Exception e,
                                                        boolean forceStacktrace) {
        OneboxRestError error = OneboxRestError.builder()
                .setHttpStatus(httpStatus)
                .setMessage(message)
                .setErrorCode(code)
                .setDeveloperMessage(developerMessage)
                .build();
        if (LOGGER.isDebugEnabled() || forceStacktrace) {
            LOGGER.error(message, e);
        }
        return new ResponseEntity<>(error, httpStatus);
    }
}
