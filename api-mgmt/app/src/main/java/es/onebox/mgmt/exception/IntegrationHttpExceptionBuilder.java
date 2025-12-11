package es.onebox.mgmt.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.datasource.http.error.ExceptionBuilder;
import es.onebox.datasource.http.status.HttpStatus;
import es.onebox.mgmt.common.URLExtractorUtils;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;


public class IntegrationHttpExceptionBuilder implements ExceptionBuilder<OneboxRestException> {
    private final ObjectMapper mapper;
    private final Map<String, ErrorCode> errorCodes;

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationHttpExceptionBuilder.class);

    public IntegrationHttpExceptionBuilder(Map<String, ErrorCode> errorCodes, ObjectMapper mapper) {
        this.errorCodes = errorCodes;
        this.mapper = mapper;
    }

    private static OneboxRestException translateHttpError(String url, Map<String, ErrorCode> errorCodes, IntegrationMsErrorException exception) {
        String errorCode = exception.getError();
        if (errorCodes.containsKey(errorCode)) {
            ErrorCode apiErrorCode = errorCodes.get(errorCode);
            if (apiErrorCode == null) {
                return null;
            }
            String error = ToStringBuilder.reflectionToString(exception, ToStringStyle.JSON_STYLE , Boolean.FALSE, IntegrationMsErrorException.class);
            LOG.error("Call to {} returns error: {}", url, error);
            if (apiErrorCode.forwardMessage()) {
                throw OneboxRestException.builder(apiErrorCode).setMessage(exception.getMessage()).build();
            }
            throw OneboxRestException.builder(apiErrorCode).build();
        }
        throw new IllegalStateException("Microservice error not mapped: " + errorCode);
    }

    @Override
    public OneboxRestException build(HttpStatus httpStatus, Request request, Response response, String responseBody) {
        String url = URLExtractorUtils.getFullURI(request);
        IntegrationMsErrorException exception;
        try {
            exception = mapper.readValue(responseBody, IntegrationMsErrorException.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error parsing JSON: " + responseBody, e);
        }
        return translateHttpError(url, errorCodes, exception);
    }
}