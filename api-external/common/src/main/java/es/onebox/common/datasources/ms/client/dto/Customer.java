package es.onebox.common.datasources.ms.client.dto;

import es.onebox.common.datasources.ms.client.dto.request.AuthOrigin;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;
    private String email;
    private String name;
    private String surname;
    private String phone;
    private String birthday;
    private String phonePrefix;
    private String idCard;
    private String idCardType;
    private String apiKey;
    private List<IdNameCodeDTO> customerTypes;
    private List<AuthOrigin> authOrigins;
    private String password;
    private String country;
    private String city;
    private String gender;
    private HashMap<String, Object> additionalProperties;
    private String iban;
    private String bic;
    private String manager;
    private Boolean isManaged;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhonePrefix() {
        return phonePrefix;
    }

    public void setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
    }

    public List<IdNameCodeDTO> getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(List<IdNameCodeDTO> customerTypes) {
        this.customerTypes = customerTypes;
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

    public List<AuthOrigin> getAuthOrigins() {
        return authOrigins;
    }

    public void setAuthOrigins(List<AuthOrigin> authOrigins) {
        this.authOrigins = authOrigins;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public Boolean getManaged() {
        return isManaged;
    }

    public void setManaged(Boolean managed) {
        isManaged = managed;
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
