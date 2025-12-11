package es.onebox.event.seasontickets.dto.renewals;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class SeasonTicketRenewalSeat implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Long seasonTicketId;
    private Long originSeasonTicketId;
    private String userId;
    private String memberId;
    private String productClientId;
    private Long entityId;
    private String entityName;
    private String email;
    private String name;
    private String surname;
    private String birthday;
    private String phoneNumber;
    private String seasonTicketName;
    private String gender;
    private String language;
    private String postalCode;
    private String address;
    private String country;
    private String countrySubdivision;
    private String city;
    private String idCard;
    private ZonedDateTime signUpDate;
    private SeatRenewal historicSeat;
    private Long historicRateId;
    private String historicRate;
    private SeatRenewal actualSeat;
    private String actualRate;
    private Long actualRateId;
    private Double balance;
    private SeatMappingStatus mappingStatus;
    private SeatRenewalStatus renewalStatus;
    private SeasonTicketRenewal renewalSettings;
    private Boolean relocation;
    private String orderCode;
    private String iban;
    private String bic;
    private String renewalSubstatus;
    private Boolean autoRenewal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public Long getOriginSeasonTicketId() {
        return originSeasonTicketId;
    }

    public void setOriginSeasonTicketId(Long originSeasonTicketId) {
        this.originSeasonTicketId = originSeasonTicketId;
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

    public String getProductClientId() {
        return productClientId;
    }

    public void setProductClientId(String productClientId) {
        this.productClientId = productClientId;
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


    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public SeatRenewal getHistoricSeat() {
        return historicSeat;
    }

    public void setHistoricSeat(SeatRenewal historicSeat) {
        this.historicSeat = historicSeat;
    }

    public Long getHistoricRateId() {
        return historicRateId;
    }

    public void setHistoricRateId(Long historicRateId) {
        this.historicRateId = historicRateId;
    }

    public String getHistoricRate() {
        return historicRate;
    }

    public void setHistoricRate(String historicRate) {
        this.historicRate = historicRate;
    }

    public SeatRenewal getActualSeat() {
        return actualSeat;
    }

    public void setActualSeat(SeatRenewal actualSeat) {
        this.actualSeat = actualSeat;
    }

    public String getActualRate() {
        return actualRate;
    }

    public void setActualRate(String actualRate) {
        this.actualRate = actualRate;
    }

    public Long getActualRateId() {
        return actualRateId;
    }

    public void setActualRateId(Long actualRateId) {
        this.actualRateId = actualRateId;
    }

    public SeatMappingStatus getMappingStatus() {
        return mappingStatus;
    }

    public void setMappingStatus(SeatMappingStatus mappingStatus) {
        this.mappingStatus = mappingStatus;
    }

    public SeatRenewalStatus getRenewalStatus() {
        return renewalStatus;
    }

    public void setRenewalStatus(SeatRenewalStatus renewalStatus) {
        this.renewalStatus = renewalStatus;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSeasonTicketName() {
        return seasonTicketName;
    }

    public void setSeasonTicketName(String seasonTicketName) {
        this.seasonTicketName = seasonTicketName;
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

    public SeasonTicketRenewal getRenewalSettings() {
        return renewalSettings;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public ZonedDateTime getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(ZonedDateTime signUpDate) {
        this.signUpDate = signUpDate;
    }

    public void setRenewalSettings(SeasonTicketRenewal renewalSettings) {
        this.renewalSettings = renewalSettings;
    }

    public Double getBalance() { return balance; }

    public void setBalance(Double balance) { this.balance = balance; }

    public Boolean getRelocation() {
        return relocation;
    }

    public void setRelocation(Boolean relocation) {
        this.relocation = relocation;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getIban() { return iban; }

    public void setIban(String iban) { this.iban = iban; }

    public String getBic() { return bic; }

    public void setBic(String bic) { this.bic = bic; }

    public String getRenewalSubstatus() { return renewalSubstatus; }

    public void setRenewalSubstatus(String renewalSubstatus) { this.renewalSubstatus = renewalSubstatus; }

    public Boolean getAutoRenewal() { return autoRenewal; }

    public void setAutoRenewal(Boolean autoRenewal) { this.autoRenewal = autoRenewal; }
}
