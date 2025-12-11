package es.onebox.event.priceengine.sorting;


import es.onebox.core.serializer.dto.request.Direction;
import es.onebox.core.serializer.dto.request.SortDirection;
import es.onebox.core.serializer.dto.request.SortOperator;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SortUtils {

    private SortUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<SortField<String>> buildSort(SortOperator<String> sortOperator, Function<String, Field<?>> mapper) {
        List<SortField<String>> orderFields = new ArrayList<>();
        if (sortOperator != null) {
            for (Object sortDirection : sortOperator.getSortDirections()) {
                SortDirection sortParam = (SortDirection) sortDirection;
                Field<?> orderField = getOrderField(mapper, (SortDirection) sortDirection, sortParam);
                if (orderField == null) {
                    continue;
                }
                orderFields.add(buildSortField(sortParam, orderField));
            }
        }
        return orderFields;
    }

    private static Field<?> getOrderField(Function<String, Field<?>> mapper, SortDirection sortDirection, SortDirection sortParam) {
        if (mapper != null) {
            Field<?> sortFieldName = mapper.apply(sortDirection.getValue().toString());
            if (sortFieldName == null) {
                return null;
            }
            return sortFieldName;
        } else {
            return DSL.field(sortParam.getValue().toString());
        }
    }


    private static SortField buildSortField(SortDirection sortParam, Field<?> orderField) {
        SortField sortField;
        if (Direction.ASC == sortParam.getDirection()) {
            sortField = orderField.asc();
        } else {
            sortField = orderField.desc();
        }
        return sortField;
    }

}
