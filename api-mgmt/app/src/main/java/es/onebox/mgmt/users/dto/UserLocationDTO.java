package es.onebox.mgmt.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.CodeNameDTO;

import java.io.Serializable;

public class UserLocationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String address;

    private String city;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("country")
    private CodeNameDTO country;

    @JsonProperty("country_subdivision")
    private CodeNameDTO countrySubdivision;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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
}
