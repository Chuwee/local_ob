package es.onebox.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.datasource.http.exception.HttpErrorException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ApiErrorUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ApiErrorUtils.class);

    private ApiErrorUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static <T> T translateHttpError(String url, Map<String, ApiExternalErrorCode> errorCodes, OneboxRestException exception) {
        String errorCode = exception.getErrorCode();
        if (errorCodes.containsKey(errorCode)) {
            ApiExternalErrorCode apiErrorCode = errorCodes.get(errorCode);
            if (apiErrorCode == null) {
                return null;
            }
            String error = ToStringBuilder.reflectionToString(exception, ToStringStyle.SHORT_PREFIX_STYLE, false, OneboxRestException.class);
            LOG.error("Call to {} returns error: {}", url, error);
            throw OneboxRestException.builder(apiErrorCode).build();
        }
        throw new IllegalStateException("Microservice error not mapped: " + errorCode);
    }

    public static <T> T translateHttpError(String url, Map<String, ApiExternalErrorCode> errorCodes, ApiException exception) {
        String errorCode = exception.getCode();
        if (errorCodes.containsKey(errorCode)) {
            ApiExternalErrorCode apiErrorCode = errorCodes.get(errorCode);
            if (apiErrorCode == null) {
                return null;
            }
            String error = ToStringBuilder.reflectionToString(exception, ToStringStyle.SHORT_PREFIX_STYLE, false, ApiException.class);
            LOG.error("Call to {} returns error: {}", url, error);
            throw OneboxRestException.builder(apiErrorCode).build();
        }
        throw new IllegalStateException("Microservice error not mapped: " + errorCode);
    }
}
