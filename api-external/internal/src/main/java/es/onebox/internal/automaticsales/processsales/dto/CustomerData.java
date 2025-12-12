package es.onebox.internal.automaticsales.processsales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.dal.dto.couch.enums.Gender;

import java.io.Serializable;
import java.util.Map;

public class CustomerData implements Serializable {

    @JsonProperty("user_id")
    private String userId;
    private String name;
    @JsonProperty("first_surname")
    private String firstSurname;
    @JsonProperty("second_surname")
    private String secondSurname;
    private String email;
    private String dni;
    private String phone;
    private String city;
    private String country;
    private Boolean allowCommercialMailing;
    private String language;
    private Gender gender;
    private InternationalPhone internationalPhone;
    private String externalClientId;
    private Map<String, Object> additionalInfo;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getSecondSurname() {
        return secondSurname;
    }

    public void setSecondSurname(String secondSurname) {
        this.secondSurname = secondSurname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getAllowCommercialMailing() {
        return allowCommercialMailing;
    }

    public void setAllowCommercialMailing(Boolean allowCommercialMailing) {
        this.allowCommercialMailing = allowCommercialMailing;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public InternationalPhone getInternationalPhone() {
        return internationalPhone;
    }

    public void setInternationalPhone(InternationalPhone internationalPhone) {
        this.internationalPhone = internationalPhone;
    }

    public String getExternalClientId() {
        return externalClientId;
    }

    public void setExternalClientId(String externalClientId) {
        this.externalClientId = externalClientId;
    }

    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, Object> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
