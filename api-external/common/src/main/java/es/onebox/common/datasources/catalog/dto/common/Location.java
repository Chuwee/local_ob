package es.onebox.common.datasources.catalog.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.catalog.dto.ChannelEventEntity;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Location extends ChannelEventEntity implements Serializable {

    private static final long serialVersionUID = 1586467636587226470L;

    private String address;
    @JsonProperty("postal_code")
    private String postalCode;
    private String city;
    @JsonProperty("country_subdivision")
    private CodeNameDTO countrySubdivision;
    private CodeNameDTO country;
    @JsonProperty("time_zone")
    private String timeZone;

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

    public CodeNameDTO getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(CodeNameDTO countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public CodeNameDTO getCountry() {
        return country;
    }

    public void setCountry(CodeNameDTO country) {
        this.country = country;
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
