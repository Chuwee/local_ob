package es.onebox.common.datasources.ms.event.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class SeasonTicketRenewalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8236910846103274940L;

    private String id;
    private String userId;
    private String memberId;
    private Long actualRateId;
    private SeatRenewal actualSeat;
    private String seasonTicketName;
    private String email;
    private String name;
    private String surname;
    private String language;
    private String iban;
    private String bic;
    private String postalCode;
    private String address;
    private String country;
    private String city;
    private ZonedDateTime signUpDate;
    private String renewalSubstatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Long getActualRateId() {
        return actualRateId;
    }

    public void setActualRateId(Long actualRateId) {
        this.actualRateId = actualRateId;
    }

    public SeatRenewal getActualSeat() {
        return actualSeat;
    }

    public void setActualSeat(SeatRenewal actualSeat) {
        this.actualSeat = actualSeat;
    }

    public String getSeasonTicketName() {
        return seasonTicketName;
    }

    public void setSeasonTicketName(String seasonTicketName) {
        this.seasonTicketName = seasonTicketName;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public ZonedDateTime getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(ZonedDateTime signUpDate) {
        this.signUpDate = signUpDate;
    }

    public String getRenewalSubstatus() {
        return renewalSubstatus;
    }

    public void setRenewalSubstatus(String renewalSubstatus) {
        this.renewalSubstatus = renewalSubstatus;
    }
}