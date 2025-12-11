package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

public class CreateDeliveryPointAddressDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "country can not be null")
    private String country;

    @NotNull(message = "countrySubdivision can not be null")
    @JsonProperty(value = "country_subdivision")
    private String countrySubdivision;

    @Length(max = 50, message = "city max size is 50")
    @NotEmpty(message = "city can not be null")
    @Pattern(regexp = "^[^|]*$", message = "Invalid characters. | is not allowed in the city")
    private String city;

    @Length(max = 200, message = "username max size is 200")
    @NotEmpty(message = "address can not be null")
    @Pattern(regexp = "^[^|]*$", message = "Invalid characters. | is not allowed in the address")
    private String address;

    @Length(max = 10, message = "zipCode max size is 10")
    @JsonProperty(value = "zip_code")
    private String zipCode;

    public CreateDeliveryPointAddressDTO() {
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(String countrySubdivision) {
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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
