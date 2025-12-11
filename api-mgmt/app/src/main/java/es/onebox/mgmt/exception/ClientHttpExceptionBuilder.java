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

public class ClientHttpExceptionBuilder implements ExceptionBuilder<OneboxRestException> {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHttpExceptionBuilder.class);

    private final Map<String, ErrorCode> errorCodes;
    private final ObjectMapper jacksonMapper;

    public ClientHttpExceptionBuilder(Map<String, ErrorCode> errorCodes, ObjectMapper jacksonMapper) {
        this.errorCodes = errorCodes;
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    public OneboxRestException build(HttpStatus httpStatus, Request request, Response response, String responseBody) {
        String url = URLExtractorUtils.getFullURI(request);
        OneboxRestException exception;
        try {
            exception = jacksonMapper.readValue(responseBody, OneboxRestException.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error parsing JSON: " + responseBody, e);
        }
        return translateHttpError(url, errorCodes, exception);
    }

    private static OneboxRestException translateHttpError(String url, Map<String, ErrorCode> errorCodes,
            OneboxRestException exception) {
        String errorCode = exception.getErrorCode();
        if (errorCodes.containsKey(errorCode)) {
            ErrorCode apiErrorCode = errorCodes.get(errorCode);
            if (apiErrorCode == null) {
                return null;
            }
            String error = ToStringBuilder.reflectionToString(exception, ToStringStyle.JSON_STYLE , Boolean.FALSE, OneboxRestException.class);
            LOG.error("Call to {} returns error: {}", url, error);
            if (apiErrorCode.forwardMessage()) {
                throw OneboxRestException.builder(apiErrorCode).setMessage(exception.getMessage()).build();
            }
            throw OneboxRestException.builder(apiErrorCode).build();
        }
        throw new IllegalStateException("Microservice error not mapped: " + errorCode);
    }
}

