package es.onebox.common.datasources;

import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.RequestHeaders;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatasourceUtils {

    public static final String COMMA = ",";
    public static final String COLON = ":";

    private DatasourceUtils() {
    }

    public static RequestHeaders prepareAuthHeader(String token) {
        return new RequestHeaders.Builder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    public static QueryParameters prepareQueryParams(Map<String, Object> params) {
        QueryParameters.Builder builder = new QueryParameters.Builder();

        for (Map.Entry<String, Object> param : params.entrySet()) {
            builder.addQueryParameter(param.getKey(), param.getValue());
        }

        return builder.build();
    }

    public static String prepareDateFilters(Map<Operator, ZonedDateTime> dates) {
        List<FilterWithOperator<ZonedDateTime>> filterDates = new ArrayList();

        for (Map.Entry<Operator, ZonedDateTime> date : dates.entrySet()) {
            if (date.getValue() != null) {
                filterDates.add(FilterWithOperator.build(date.getKey(), date.getValue()));
            }
        }

        return dateFilterToString(filterDates);
    }

    public static <T extends Object> void addParameters(RequestBuilder requestBuilder, String paramName, List<T> values) {
        if (values != null) {
            values.forEach(value -> requestBuilder.addParameter(paramName, String.valueOf(value)));
        }
    }

    private static String dateFilterToString(List<FilterWithOperator<ZonedDateTime>> dates) {
        List<String> result;
        result = dates.stream().map(DatasourceUtils::addFilter).collect(Collectors.toList());
        return StringUtils.join(result, COMMA);
    }

    private static String addFilter(FilterWithOperator<ZonedDateTime> date) {
        StringBuilder sb = new StringBuilder();
        sb.append(date.getOperator().getKey());
        sb.append(COLON);
        sb.append(date.getValue());
        return sb.toString();
    }
}
