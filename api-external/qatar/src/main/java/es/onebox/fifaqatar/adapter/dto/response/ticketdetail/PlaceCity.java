package es.onebox.fifaqatar.adapter.dto.response.ticketdetail;

import java.io.Serial;
import java.io.Serializable;

public class PlaceCity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4033112969488957679L;

    private Integer id;
    private String code;
    private String name;
    private String country;
    private String timezone;
    private Double latitude;
    private Double  longitude;
    private String locale; // ex: "en"

    //TODO map more fields if we need it


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

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

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
