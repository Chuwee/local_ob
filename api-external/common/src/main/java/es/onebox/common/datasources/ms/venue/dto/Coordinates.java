package es.onebox.common.datasources.ms.venue.dto;

import java.io.Serial;
import java.io.Serializable;

public class Coordinates implements Serializable {

    @Serial
    private static final long serialVersionUID = -2503926829910100160L;

    private Double latitude;
    private Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
