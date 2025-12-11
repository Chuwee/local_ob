package es.onebox.event.common.utils;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;

public class JooqUtils {

    private JooqUtils() {
        throw new UnsupportedOperationException();
    }

    public static Condition filterDateWithOperatorToCondition(Condition conditions, FilterWithOperator<ZonedDateTime> value, Field field, Operator operator) {
        return filterDateWithOperatorToCondition(conditions, value, field, operator, null);
    }

    public static Condition filterDateWithOperatorToCondition(Condition conditions, List<FilterWithOperator<ZonedDateTime>> value, Field field, Operator operator) {
        if(CollectionUtils.isNotEmpty(value)) {
            for (FilterWithOperator<ZonedDateTime> filterWithOperator : value) {
                conditions = filterDateWithOperatorToCondition(conditions, filterWithOperator, field, operator);
            }
        }
        return conditions;
    }

    public static Condition filterDateWithOperatorToCondition(Condition conditions,
            FilterWithOperator<ZonedDateTime> value, Field<Timestamp> field, Operator operator,
            Field<Timestamp> fallbackField) {
        if (value != null) {
            Timestamp timestamp = Timestamp.from(value.getValue().toInstant());
            Condition condition = timeOperatorCondition(field, value.getOperator(), timestamp);
            if (fallbackField != null) {
                condition = condition
                        .or(field.isNull().and(timeOperatorCondition(fallbackField, value.getOperator(), timestamp)));
            }
            conditions = DSL.condition(operator, conditions, condition);
        }
        return conditions;
    }

    private static Condition timeOperatorCondition(Field<Timestamp> field,
                                                   es.onebox.core.serializer.dto.request.Operator operator, Timestamp timestamp) {
        switch (operator) {
            case NOT_EQUALS:
                return field.notEqual(timestamp);
            case GREATER_THAN:
                return field.greaterThan(timestamp);
            case GREATER_THAN_OR_EQUALS:
                return field.greaterOrEqual(timestamp);
            case LESS_THAN:
                return field.lessThan(timestamp);
            case LESS_THAN_OR_EQUALS:
                return field.lessOrEqual(timestamp);
            default:
                return field.eq(timestamp);
        }
    }

    public static Condition addConditionEquals(Condition conditions, Field field, Object value) {
        if (value != null) {
            return conditions.and(field.eq(value));
        }
        return conditions;
    }

    public static Condition addConditionIn(Condition conditions, Field field, List<?> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            return conditions.and(field.in(values));
        }
        return conditions;
    }

    public static Condition addConditionLessOrEquals(Condition conditions, Field<Timestamp> field, ZonedDateTime date) {
        if (date != null) {
            return conditions.and(field.le(Timestamp.from(date.toInstant())));
        }
        return conditions;
    }

    public static Condition addConditionGreaterOrEquals(Condition conditions, Field<Timestamp> field,
            ZonedDateTime date) {
        if (date != null) {
            return conditions.and(field.ge(Timestamp.from(date.toInstant())));
        }
        return conditions;
    }

    public static Condition addDateWithListOperatorConditions(Condition conditions, List<FilterWithOperator<ZonedDateTime>> list, Field field, Operator operator) {
        for (FilterWithOperator<ZonedDateTime> filter : list) {
            conditions = filterDateWithOperatorToCondition(conditions, filter, field, operator, null);
        }
        return conditions;
    }


    public static <T extends BaseRequestFilter> void fillFilter (SelectConditionStep query, T filter) {
        if (filter != null) {
            if (filter.getLimit() != null) {
                query.limit(filter.getLimit().intValue());
            }
            if (filter.getOffset() != null) {
                query.offset(filter.getOffset().intValue());
            }
        }
    }
}
