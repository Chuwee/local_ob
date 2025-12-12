package es.onebox.common.datasources.common.dto;

import java.io.Serializable;

public class Location implements Serializable {

    private static final long serialVersionUID = 6435048499827213066L;
    private Double lat;
    private Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
