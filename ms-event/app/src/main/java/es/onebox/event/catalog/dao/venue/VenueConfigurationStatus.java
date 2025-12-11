package es.onebox.event.catalog.dao.venue;

import java.util.Arrays;

public enum VenueConfigurationStatus {

    DELETED(0),
    ACTIVE(1),
    PROCESSING(2),
    ERROR(3);

    private Integer status;

    private VenueConfigurationStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public static VenueConfigurationStatus fromStatus(Integer status) {
        return Arrays.stream(VenueConfigurationStatus.values())
                .filter(v -> v.status.equals(status))
                .findFirst()
                .orElse(null);
    }
}
