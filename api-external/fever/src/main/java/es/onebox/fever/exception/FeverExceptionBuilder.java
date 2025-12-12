package es.onebox.fever.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.datasource.http.error.ExceptionBuilder;
import es.onebox.datasource.http.status.HttpStatus;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.Map;

public record FeverExceptionBuilder(
        Map<String, ApiExternalErrorCode> errorCodes,
        ObjectMapper jacksonMapper) implements ExceptionBuilder<FeverException> {

    private static final Logger LOG = LoggerFactory.getLogger(FeverExceptionBuilder.class);

    @Override
    public FeverException build(HttpStatus httpStatus, Request request, Response response, String responseBody) {
        String url = request.url().encodedPath();
        FeverException exception;
        try {
            exception = jacksonMapper.readValue(responseBody, FeverException.class);
            org.springframework.http.HttpStatus springHttpStatus = org.springframework.http.HttpStatus.valueOf(
                    response.code());
            exception.setHttpStatus(springHttpStatus);
            ApiExternalErrorCode errorCode = errorCodes.entrySet().stream()
                                                       .filter(entry -> entry.getValue().getHttpStatus().equals(
                                                               springHttpStatus))
                                                       .findFirst().map(Map.Entry::getValue)
                                                       .orElse(ApiExternalErrorCode.GENERIC_ERROR);
            exception.setErrorCode(errorCode.getErrorCode());
            exception.setMessage(errorCode.getMessage());
            if (response.headers().toMultimap().containsKey(HttpHeaders.RETRY_AFTER)) {
                exception.getHeaders()
                         .put(HttpHeaders.RETRY_AFTER, response.headers().toMultimap().get(HttpHeaders.RETRY_AFTER));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error parsing JSON: " + responseBody, e);
        }
        String error = ToStringBuilder.reflectionToString(exception, ToStringStyle.SHORT_PREFIX_STYLE, false,
                                                          FeverException.class);
        LOG.error("Call to {} returns error: {}", url, error);
        return exception;
    }

}
