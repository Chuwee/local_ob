package es.onebox.event.datasources.utils;

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
        return DatasourceUtils.parseError(url, errorCodes, exception);
    }
}
