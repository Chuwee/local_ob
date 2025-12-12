package es.onebox.common.datasources.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Venue implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Entity entity;
    private Location location;

    private String address;
    @JsonProperty("postal_code")
    private String postalCode;
    private String city;
    private CodeNameDTO country;
    @JsonProperty("country_subdivision")
    private CodeNameDTO countrySubdivision;
    @JsonProperty("time_zone")
    private String timeZone;

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

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public CodeNameDTO getCountry() {
        return country;
    }

    public void setCountry(CodeNameDTO country) {
        this.country = country;
    }

    public CodeNameDTO getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(CodeNameDTO countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
