package es.onebox.fifaqatar.adapter.dto.response.ticketdetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class TicketPlace implements Serializable {

    @Serial
    private static final long serialVersionUID = -3683617153976795232L;

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("address")
    private String address;
    @JsonProperty("city")
    private PlaceCity city;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("locale")
    private String locale;
    @JsonProperty("hidden")
    private Boolean hidden;
    @JsonProperty("metro_stations")
    private List metroStations; //Just an empty list

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public PlaceCity getCity() {
        return city;
    }

    public void setCity(PlaceCity city) {
        this.city = city;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public List getMetroStations() {
        return metroStations;
    }

    public void setMetroStations(List metroStations) {
        this.metroStations = metroStations;
    }
}
