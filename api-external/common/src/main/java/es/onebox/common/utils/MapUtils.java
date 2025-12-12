package es.onebox.common.utils;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapUtils {
    public static <S,T> Map<S, List<T>> reverseMap(Map<T, List<S>> map) {
        return org.apache.commons.collections4.MapUtils.isEmpty(map) ? null : map.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(s -> new AbstractMap.SimpleEntry<>(s, e.getKey())))
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                        Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toList())));
    }
}
