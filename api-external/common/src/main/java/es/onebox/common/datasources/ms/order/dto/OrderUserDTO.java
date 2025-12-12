package es.onebox.common.datasources.ms.order.dto;

import es.onebox.dal.dto.couch.order.InternationalPhoneDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class OrderUserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4172590103439425888L;

    private String name;
    private String surname;
    private String address;
    private String idNumber;
    private String zipCode;
    private String city;
    private String receiptEmail;
    private String userId;
    private String email;
    private String phone;
    private String country;
    private InternationalPhoneDTO internationalPhone;
    private Map<String, Object> additionalInfo;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getReceiptEmail() {
        return receiptEmail;
    }

    public void setReceiptEmail(String receiptEmail) {
        this.receiptEmail = receiptEmail;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public InternationalPhoneDTO getInternationalPhone() {
        return internationalPhone;
    }

    public void setInternationalPhone(InternationalPhoneDTO internationalPhone) {
        this.internationalPhone = internationalPhone;
    }

    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, Object> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
