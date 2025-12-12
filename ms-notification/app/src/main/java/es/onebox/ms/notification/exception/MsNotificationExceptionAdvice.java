package es.onebox.ms.notification.exception;

import es.onebox.core.exception.OneboxRestError;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.webmvc.converter.message.EntityHttpMessageConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class MsNotificationExceptionAdvice {

    @ExceptionHandler(OneboxRestException.class)
    private @ResponseBody ResponseEntity<OneboxRestError> errorHandler(HttpServletRequest req, OneboxRestException e) {
        return new ResponseEntity<>(OneboxRestError.of(e), e.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    private @ResponseBody ResponseEntity<OneboxRestError> errorHandler(HttpServletRequest req, Exception e) {
        return new ResponseEntity<>(OneboxRestError.builder(MsNotificationErrorCode.GENERIC_REST_EXCEPTION)
                .setThrowable(e)
                .build(), MsNotificationErrorCode.GENERIC_REST_EXCEPTION.getHttpStatus());
    }

    private static final HttpMessageConverter<Object> jsonConvert;

    static {
        jsonConvert = EntityHttpMessageConverter.basicMessageConverter();
    }

}
