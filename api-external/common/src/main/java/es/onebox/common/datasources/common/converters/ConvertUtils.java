package es.onebox.common.datasources.common.converters;

import es.onebox.common.datasources.common.dto.FiltrableField;
import es.onebox.core.serializer.dto.request.SortDirection;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.datasource.http.QueryParameters;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class ConvertUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertUtils.class);

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

    // Converts locale to languageTag IETF BCP47 (es_ES to es-ES)
    public static String toLanguageTag(String locale) {
        try {
            return LocaleUtils.toLocale(locale).toLanguageTag();
        } catch (Exception e) {
            LOGGER.warn("Error converting locale {} to languageTag - Keep original", locale);
            return locale;
        }
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


}
