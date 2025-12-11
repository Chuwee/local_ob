package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;

public class SeasonTicketRenewalSeat implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String memberId;
    private String productClientId;
    private Integer seasonTicketId;
    private String email;
    private String name;
    private Long entityId;
    private String entityName;
    private String surname;
    private String birthday;
    private String phoneNumber;
    private String seasonTicketName;
    private String postalCode;
    private String address;
    private SeatRenewal historicSeat;
    private String historicRate;
    private SeatRenewal actualSeat;
    private String actualRate;
    private Integer actualRateId;
    private Double balance;
    private SeatMappingStatus mappingStatus;
    private SeatRenewalStatus renewalStatus;
    private SeasonTicketRenewal renewalSettings;
    private String orderCode;
    private Boolean autoRenewal;
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

    public Long getEntityId() {return entityId;}

    public void setEntityId(Long entityId) {this.entityId = entityId;}

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getPhoneNumber() {return phoneNumber;}

    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}

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

    public SeasonTicketRenewal getRenewalSettings() {
        return renewalSettings;
    }

    public void setRenewalSettings(SeasonTicketRenewal renewalSettings) {
        this.renewalSettings = renewalSettings;
    }

    public Integer getActualRateId() {return actualRateId;}

    public void setActualRateId(Integer actualRateId) {this.actualRateId = actualRateId;}

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getSeasonTicketId() {return seasonTicketId;}

    public void setSeasonTicketId(Integer seasonTicketId) {this.seasonTicketId = seasonTicketId;}

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public String getRenewalSubstatus() {
        return renewalSubstatus;
    }

    public void setRenewalSubstatus(String renewalSubstatus) {
        this.renewalSubstatus = renewalSubstatus;
    }
}
