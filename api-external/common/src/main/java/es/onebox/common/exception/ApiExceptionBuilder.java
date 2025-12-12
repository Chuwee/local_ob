package es.onebox.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.datasource.http.error.ExceptionBuilder;
import es.onebox.datasource.http.status.HttpStatus;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class ApiExceptionBuilder implements ExceptionBuilder<ApiException> {

    private final Map<String, ApiExternalErrorCode> errorCodes;
    private final ObjectMapper jacksonMapper;

    public ApiExceptionBuilder(Map<String, ApiExternalErrorCode> errorCodes, ObjectMapper jacksonMapper) {
        this.errorCodes = errorCodes;
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    public ApiException build(HttpStatus httpStatus, Request request, Response response, String responseBody) {
        String url = request.url().encodedPath();
        ApiException exception;
        try {
            exception = jacksonMapper.readValue(responseBody, ApiException.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error parsing JSON: " + responseBody, e);
        }
        return ApiErrorUtils.translateHttpError(url, errorCodes, exception);
    }
}
