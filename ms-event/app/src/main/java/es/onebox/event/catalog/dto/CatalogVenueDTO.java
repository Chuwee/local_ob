package es.onebox.event.catalog.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CatalogVenueDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5559429843768553642L;
    private Long id;
    private String name;
    private Long entityId;
    private CatalogLocationDTO country;
    private CatalogLocationDTO countrySubdivision;
    private String city;
    private String postalCode;
    private String address;
    private String timeZone;
    private String image;
    private String googlePlaceId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEntityId() { return entityId; }

    public void setEntityId(Long entityId) {this.entityId = entityId; }

    public CatalogLocationDTO getCountry() {
        return country;
    }

    public void setCountry(CatalogLocationDTO country) {
        this.country = country;
    }

    public CatalogLocationDTO getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(CatalogLocationDTO countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGooglePlaceId() { return googlePlaceId; }

    public void setGooglePlaceId(String googlePlaceId) { this.googlePlaceId = googlePlaceId; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
