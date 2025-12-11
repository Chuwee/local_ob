package es.onebox.event.catalog.elasticsearch.enums;


import java.util.Arrays;

public enum PeriodRangeType {

    ALL(0), RANGE(1);

    private final Integer id;

    PeriodRangeType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PeriodRangeType getById(Integer id) {
        return Arrays.stream(values())
                .filter( item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
