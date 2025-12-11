package es.onebox.mgmt.datasources.ms.channel.dto.reviews;

import java.util.Objects;
import java.util.stream.Stream;

public enum ChannelReviewTimeUnit {

    HOURS(0),
    DAYS(1);

    private final Integer value;

    ChannelReviewTimeUnit(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static ChannelReviewTimeUnit getById(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(values())
                .filter(cr -> Objects.equals(cr.getValue(), id))
                .findAny()
                .orElse(null);
    }
}
