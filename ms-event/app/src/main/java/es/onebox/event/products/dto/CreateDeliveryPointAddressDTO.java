package es.onebox.event.products.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

public class CreateDeliveryPointAddressDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "countryId can not be null")
    private Long countryId;

    @NotNull(message = "countrySubdivisionId can not be null")
    private Long countrySubdivisionId;

    @Length(max = 50, message = "city max size is 50")
    @NotEmpty(message = "city can not be null")
    @Pattern(regexp = "^[^|]*$", message = "Invalid characters. | is not allowed in the city")
    private String city;

    @Length(max = 200, message = "address max size is 200")
    @NotEmpty(message = "address can not be null")
    @Pattern(regexp = "^[^|]*$", message = "Invalid characters. | is not allowed in the address")
    private String address;

    @Length(max = 10, message = "zipCode max size is 10")
    private String zipCode;


    public CreateDeliveryPointAddressDTO() {
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getCountrySubdivisionId() {
        return countrySubdivisionId;
    }

    public void setCountrySubdivisionId(Long countrySubdivisionId) {
        this.countrySubdivisionId = countrySubdivisionId;
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

