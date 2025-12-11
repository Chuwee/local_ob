package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serializable;
import java.util.stream.Stream;

public enum VisibilityType implements Serializable {

    FULL(1),
    PARTIAL(2),
    NONE(3),
    SIDE(4);

    private int status;

    public int getStatus() {
        return status;
    }

    VisibilityType(int status) {
        this.status = status;
    }

    public static VisibilityType byId(Integer id) {
        return Stream.of(VisibilityType.values())
                .filter(v -> v.getStatus() == id)
                .findFirst()
                .orElse(null);
    }

}
