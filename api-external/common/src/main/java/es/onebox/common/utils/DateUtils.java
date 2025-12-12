package es.onebox.common.utils;

import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

    public static List<FilterWithOperator<ZonedDateTime>> getDate(ZonedDateTime zonedDateTime, Operator operator) {
        List<FilterWithOperator<ZonedDateTime>> filter = new ArrayList<>();
        FilterWithOperator<ZonedDateTime> filterWithOperator = new FilterWithOperator<>();
        ZonedDateTime dateWithoutOffset = ZonedDateTime.of(zonedDateTime.toOffsetDateTime().toLocalDateTime(), ZoneOffset.UTC);
        filterWithOperator.setValue(dateWithoutOffset);
        filterWithOperator.setOperator(operator);
        filter.add(filterWithOperator);
        return filter;
    }

}
