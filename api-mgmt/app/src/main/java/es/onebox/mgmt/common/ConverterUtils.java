package es.onebox.mgmt.common;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.request.SortDirection;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;

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

    public static String toLanguage(String locale) {
        try {
            return Locale.forLanguageTag(locale).getLanguage();
        } catch (Exception e) {
            LOGGER.warn("Error converting locale {} to language - Keep original", locale);
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
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "language format must be IETF (es-ES)", null);
        }
        if (!languages.containsKey(locale)) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "language not found", null);
        }
        return locale;
    }

    public static String checkLanguageByIds(String languageTag, Map<Long, String> languagesByIds) {
        String locale = ConverterUtils.toLocale(languageTag);
        if (locale == null || locale.isEmpty()) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "language format must be IETF (es-ES)", null);
        }
        if (!languagesByIds.containsValue(locale)) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "language not found", null);
        }
        return locale;
    }

    public static void checkSortFields(SortOperator<String> sort, QueryParameters.Builder params, Function<String, FiltrableField> fieldFilter) {
        if (sort != null) {
            SortOperator<String> requestSort = new SortOperator<>();
            for (SortDirection<String> sortDirection : sort.getSortDirections()) {
                FiltrableField sortValue = fieldFilter.apply(sortDirection.getValue());
                if (sortValue != null) {
                    requestSort.addDirection(sortDirection.getDirection(), sortValue.getDtoName());
                }
            }
            if (!requestSort.getSortDirections().isEmpty()) {
                params.addQueryParameter("sort", requestSort.toString());
            }
        }
    }

    public static SortOperator<String> checkSortFields(SortOperator<String> sort, Function<String, FiltrableField> fieldFilter) {
        if (sort != null) {
            SortOperator<String> requestSort = new SortOperator<>();
            for (SortDirection<String> sortDirection : sort.getSortDirections()) {
                FiltrableField sortValue = fieldFilter.apply(sortDirection.getValue());
                if (sortValue != null) {
                    requestSort.addDirection(sortDirection.getDirection(), sortValue.getDtoName());
                }
            }
            return requestSort;
        }
        return null;
    }

    public static void checkFilterFields(List<String> fields, QueryParameters.Builder params, Function<String, FiltrableField> fieldFilter) {
        if (fields != null) {
            for (String field : fields) {
                FiltrableField filterField = fieldFilter.apply(field);
                if (filterField != null) {
                    params.addQueryParameter("fields", filterField.getDtoName());
                }
            }
        }
    }

    public static List<String> checkFilterFields(List<String> fields, Function<String, FiltrableField> fieldFilter) {
        if (fields != null) {
            return fields.stream().map(fieldFilter).filter(Objects::nonNull).map(FiltrableField::getDtoName).toList();
        }
        return null;
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
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, fieldName + " is mandatory and must be above 0", null);
        }
    }

    public static void checkField(String fieldValue, String fieldName) {
        if (StringUtils.isBlank(fieldValue)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, fieldName + " is mandatory", null);
        }
    }

    public static void checkField(Object fieldValue, String fieldName) {
        if (fieldValue == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, fieldName + " is mandatory", null);
        }
    }

    public static Integer getIntLimitlessValue(LimitlessValueDTO value) {
        Long limitlessValue = getLimitlessValue(value);
        return limitlessValue != null ? limitlessValue.intValue() : null;
    }

    public static Long getLimitlessValue(LimitlessValueDTO value) {
        if (value != null) {
            if (value.getValue() != null && value.getValue() >= 0) {
                return value.getValue();
            } else if (value.getType() != null && value.getType().equals(LimitlessValueType.UNLIMITED)) {
                return -1L;
            }
        }
        return null;
    }

    public static void addQueryParameter(QueryParameters.Builder builder, String name, Object value) {
        if (value != null) {
            builder.addQueryParameter(name, value);
        }
    }

    public static <K1, V1, K2, V2> Map<K2, V2> transformMapKeysAndValues(Map<K1, V1> map,
                                                                         Function<K1, K2> keyTransform,
                                                                         Function<V1, V2> valueTransform) {
        return transformMapKeysAndValues(map, keyTransform, valueTransform, (existing, duplicate) -> existing);
    }

    public static <K1, V1, K2, V2> Map<K2, V2> transformMapKeysAndValues(Map<K1, V1> map,
                                                                         Function<K1, K2> keyTransform,
                                                                         Function<V1, V2> valueTransform,
                                                                         BinaryOperator<V2> mergeFunction) {
        if (map == null) {
            return null;
        }
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> keyTransform.apply(entry.getKey()),
                        entry -> valueTransform.apply(entry.getValue()),
                        mergeFunction
                ));
    }

    public static <T> Map<String, T> changeMapLangKeyToUnderScore(Map<String, T> in) {
        if (MapUtils.isNotEmpty(in)) {
            Map<String, T> newNames = new HashMap<>();
            in.forEach( (k,v) -> newNames.put(langKeyToUnderscore(k), v));
            return newNames;
        }
        return null;
    }

    private static String langKeyToUnderscore(String k) {
        return k.replace('-', '_');
    }

    public static <T> Map<String, T> changeMapLangKeyToKevapCase(Map<String, T> texts) {
        if (MapUtils.isNotEmpty(texts)) {
            Map<String, T> newTexts = new HashMap<>();
            texts.forEach( (k,v) -> newTexts.put(langKeyToKevapCase(k), v));
            return newTexts;
        }
        return null;
    }

    private static String langKeyToKevapCase(String k) {
        return k.replace('_', '-');
    }
}
