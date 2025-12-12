package es.onebox.bepass.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.datasource.http.error.ExceptionBuilder;
import es.onebox.datasource.http.status.HttpStatus;
import es.onebox.bepass.datasources.bepass.dto.BepassError;
import es.onebox.bepass.datasources.bepass.dto.BepassFieldError;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class BepassHttpExceptionBuilder implements ExceptionBuilder<OneboxRestException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BepassHttpExceptionBuilder.class);

    private final ObjectMapper jacksonMapper;
    private final Map<String, BepassErrorCode> errorCodes;

    public BepassHttpExceptionBuilder(ObjectMapper jacksonMapper, Map<String, BepassErrorCode> errorCodes) {
        this.jacksonMapper = jacksonMapper;
        this.errorCodes = errorCodes;
    }

    @Override
    public OneboxRestException build(HttpStatus status, Request request, Response response, String responseBody) {
        String url = request.url().encodedPath();
        BepassError exception;
        try {
            exception = jacksonMapper.readValue(responseBody, BepassError.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error parsing JSON: " + responseBody, e);
        }
        return translateHttpError(url, errorCodes, exception);
    }

    public static <T> T translateHttpError(String url, Map<String, BepassErrorCode> errorCodes, BepassError exception) {
        String errorCode = null;

        if (CollectionUtils.isNotEmpty(exception.errors())) {
            BepassFieldError bepassFieldError = exception.errors().get(0);
            errorCode = bepassFieldError.field();
        } else if (CollectionUtils.isEmpty(exception.message())) {
            errorCode = exception.status();
        } else {
            errorCode = exception.message().get(0);
        }
        if (errorCodes.containsKey(errorCode)) {
            BepassErrorCode bepassErrorCode = errorCodes.get(errorCode);
            if (bepassErrorCode == null) {
                return null;
            }
            LOGGER.error("Call to {} returns error: {}", url, errorCode);
            throw OneboxRestException.builder(bepassErrorCode).build();
        }
        throw new IllegalStateException("Bepass error not mapped: " + errorCode);
    }
}
