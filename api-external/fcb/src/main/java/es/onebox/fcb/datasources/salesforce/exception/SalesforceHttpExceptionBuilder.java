package es.onebox.fcb.datasources.salesforce.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.datasource.http.error.ExceptionBuilder;
import es.onebox.datasource.http.status.HttpStatus;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class SalesforceHttpExceptionBuilder implements ExceptionBuilder<OneboxRestException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesforceHttpExceptionBuilder.class);

    private final ObjectMapper jacksonMapper;

    public SalesforceHttpExceptionBuilder(Map<String, ApiExternalErrorCode> errorCodes, ObjectMapper jacksonMapper) {
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    public OneboxRestException build(HttpStatus httpStatus, Request request, Response response, String responseBody) {
        String url = request.url().encodedPath();
        SalesforceException exception;
        try {
            exception = jacksonMapper.readValue(responseBody, SalesforceException.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error parsing JSON: " + responseBody, e);
        }
        return translateHttpError(httpStatus, url, exception);
    }

    public static <T> T translateHttpError(HttpStatus httpStatus, String url, SalesforceException exception) {
        String error = ToStringBuilder.reflectionToString(exception, ToStringStyle.SHORT_PREFIX_STYLE, false, SalesforceException.class);
        LOGGER.error("[FCB WEBHOOK] Call to {} returns error: {}", url, error);
        OneboxRestException oneboxRestException = new OneboxRestException();
        oneboxRestException.setMessage(exception.getMessage());
        oneboxRestException.setErrorCode(exception.getErrorcode());
        oneboxRestException.setHttpStatus(org.springframework.http.HttpStatus.valueOf(httpStatus.value()));
        throw oneboxRestException;
    }
}
