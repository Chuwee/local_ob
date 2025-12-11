package es.onebox.mgmt.datasources.ms.channel.dto.reviews;

import java.util.Objects;
import java.util.stream.Stream;

public enum ChannelReviewScope {

    EVENT(0),
    SESSION(1);

    private final Integer value;

    ChannelReviewScope(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static ChannelReviewScope getById(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(values())
                .filter(cr -> Objects.equals(cr.getValue(), id))
                .findAny()
                .orElse(null);
    }
}
