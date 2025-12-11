package es.onebox.event.catalog.converter.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChannelEventConversionUtils {

    private ChannelEventConversionUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static <T> List<Long> arrayOfUniqueIdsByFieldName(List<T> list, Function<T, Long> getter) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(getter)
                .distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
