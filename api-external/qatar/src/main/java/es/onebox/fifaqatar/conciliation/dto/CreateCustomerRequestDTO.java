package es.onebox.fifaqatar.conciliation.dto;

import es.onebox.common.datasources.ms.client.dto.request.CreateCustomerRequest;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class CreateCustomerRequestDTO extends CreateCustomerRequest {
    @Serial
    private static final long serialVersionUID = 1191656417102133712L;
    private String type;
    private String country;
    private String city;
    private String idCard;
    private String idCardType;
    private String phone;
    private String phonePrefix;
    private String gender;
    private HashMap<String, Object> additionalProperties;

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
}
