package es.onebox.channels.catalog.eci.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ECIAddressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String address;
    private String city;
    private String province;
    private String country;
    private String postalCode;
    @JsonProperty("city_ine_code")
    private String cityINECode;
    @JsonProperty("province_ine_code")
    private String provinceINECode;
    @JsonProperty("country_iso_code")
    private String countryISOCode;

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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCityINECode() {
        return cityINECode;
    }

    public void setCityINECode(String cityINECode) {
        this.cityINECode = cityINECode;
    }

    public String getProvinceINECode() {
        return provinceINECode;
    }

    public void setProvinceINECode(String provinceINECode) {
        this.provinceINECode = provinceINECode;
    }

    public String getCountryISOCode() {
        return countryISOCode;
    }

    public void setCountryISOCode(String countryISOCode) {
        this.countryISOCode = countryISOCode;
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
