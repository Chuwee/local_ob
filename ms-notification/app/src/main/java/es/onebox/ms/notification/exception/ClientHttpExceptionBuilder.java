package es.onebox.ms.notification.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.datasource.http.error.ExceptionBuilder;
import es.onebox.datasource.http.status.HttpStatus;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class ClientHttpExceptionBuilder implements ExceptionBuilder<OneboxRestException> {

    private final Map<String, ErrorCode> errorCodes;
    private final ObjectMapper jacksonMapper;

    public ClientHttpExceptionBuilder(Map<String, ErrorCode> errorCodes, ObjectMapper jacksonMapper) {
        this.errorCodes = errorCodes;
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    public OneboxRestException build(HttpStatus httpStatus, Request request, Response response, String responseBody) {
        String url = request.url().encodedPath();
        OneboxRestException exception;
        try {
            exception = jacksonMapper.readValue(responseBody, OneboxRestException.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error parsing JSON: " + responseBody, e);
        }
        return parseError(url, errorCodes, exception);
    }

    private static OneboxRestException parseError(String url, Map<String, ErrorCode> errorCodes, OneboxRestException e) {
        String code = e.getErrorCode();
        if (errorCodes.containsKey(code)) {
            ErrorCode errorCode = errorCodes.get(code);
            if (errorCode == null) {
                return null;
            }
            throw OneboxRestException.builder(errorCode).build();
        } else {
            throw new IllegalStateException("Unknown error code " + code + " calling " + url, e);
        }
    }
}
