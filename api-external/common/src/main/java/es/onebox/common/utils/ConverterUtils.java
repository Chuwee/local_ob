package es.onebox.common.utils;

import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.datasource.http.QueryParameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class ConverterUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConverterUtils.class);

    private ConverterUtils() {
    }

    // Converts locale to languageTag IETF BCP47 (es_ES to es-ES)
    public static String toLanguageTag(String locale) {
        try {
            return LocaleUtils.toLocale(locale).toLanguageTag();
        } catch (Exception e) {
            LOGGER.warn("Error converting locale {} to languageTag - Keep original", locale);
            return locale;
        }
    }

    // Converts languageTag IETF BCP47 to locale (es-ES to es_ES)
    public static String toLocale(String languageTag) {
        try {
            return Locale.forLanguageTag(languageTag).toString();
        } catch (Exception e) {
            LOGGER.warn("Error converting languageTag {} to locale - Keep original", languageTag);
            return null;
        }
    }

    public static String checkLanguage(String languageTag, Map<String, Long> languages) {
        String locale = ConverterUtils.toLocale(languageTag);
        if (locale == null || locale.isEmpty()) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER, "language format must be IETF (es-ES)", null);
        }
        if (!languages.containsKey(locale)) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER, "language not found", null);
        }
        return locale;
    }

    public static String checkLanguageByIds(String languageTag, Map<Long, String> languagesByIds) {
        String locale = ConverterUtils.toLocale(languageTag);
        if (locale == null || locale.isEmpty()) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER, "language format must be IETF (es-ES)", null);
        }
        if (!languagesByIds.containsValue(locale)) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER, "language not found", null);
        }
        return locale;
    }

    public static <T> void updateField(Consumer<T> target, T sourceField) {
        if (sourceField != null) {
            target.accept(sourceField);
        }
    }

    public static void addFreeSearch(String freeSearch, QueryParameters.Builder params) {
        if (StringUtils.isNotBlank(freeSearch)) {
            params.addQueryParameter("freeSearch", freeSearch);
        }
    }

    public static void addSeasonTicketId(Integer seasonTicketId, QueryParameters.Builder params) {
        if (seasonTicketId != null) {
            params.addQueryParameter("seasonTicketId", seasonTicketId);
        }
    }

    public static void addEntityId(Long entityId, QueryParameters.Builder params) {
        if (entityId != null) {
            params.addQueryParameter("entityId", entityId);
        }
    }

    public static void checkField(Number fieldValue, String fieldName) {
        if (fieldValue == null || fieldValue.longValue() <= 0) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER, fieldName + " is mandatory and must be above 0", null);
        }
    }

    public static void checkField(String fieldValue, String fieldName) {
        if (StringUtils.isBlank(fieldValue)) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER, fieldName + " is mandatory", null);
        }
    }

    public static void checkField(Object fieldValue, String fieldName) {
        if (fieldValue == null) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER, fieldName + " is mandatory", null);
        }
    }

    public static void addQueryParameter(QueryParameters.Builder builder, String name, Object value) {
        if (value != null) {
            builder.addQueryParameter(name, value);
        }
    }
}
