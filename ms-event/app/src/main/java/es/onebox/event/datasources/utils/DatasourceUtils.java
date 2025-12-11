package es.onebox.event.datasources.utils;

import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestException;

import java.util.Map;

public class DatasourceUtils {

    private DatasourceUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static <T> T parseError(String url, Map<String, ErrorCode> errorCodes, OneboxRestException e) {
        return parseError(url, errorCodes, e, null);
    }

    public static <T> T parseError(String url, Map<String, ErrorCode> errorCodes, OneboxRestException e, T defaultValue) {
        String code = e.getErrorCode();
        if (errorCodes.containsKey(code)) {
            ErrorCode errorCode = errorCodes.get(code);
            if (errorCode == null) {
                return defaultValue;
            }
            throw OneboxRestException.builder(errorCode).build();
        } else {
            throw new IllegalStateException("Unknown error code " + code + " calling " + url, e);
        }
    }
}
