package es.onebox.fifaqatar.conciliation.dto;


import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.client.dto.request.AuthOrigin;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateCustomerRequestDTO extends Customer {

    @Serial
    private static final long serialVersionUID = -14622088935163779L;

    private String name;
    private String surname;
    private String status;
    private String type;
    private String country;
    private String city;
    private String idCard;
    private String idCardType;
    private String phone;
    private String phonePrefix;
    private String gender;
    private HashMap<String, Object> additionalProperties;
    private List<AuthOrigin> authOrigins;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(String idCardType) {
        this.idCardType = idCardType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhonePrefix() {
        return phonePrefix;
    }

    public void setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public HashMap<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(HashMap<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public List<AuthOrigin> getAuthOrigins() {
        return authOrigins;
    }

    public void setAuthOrigins(List<AuthOrigin> authOrigins) {
        this.authOrigins = authOrigins;
    }
}
