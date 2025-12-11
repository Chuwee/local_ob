package es.onebox.event.seasontickets.dao.dto;

import es.onebox.elasticsearch.annotation.ElasticRepository;
import es.onebox.elasticsearch.dao.ElasticDocument;
import es.onebox.event.seasontickets.elasticsearch.RenewalsDataUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

@ElasticRepository(indexName = RenewalsDataUtils.RENEWAL_INDEX, queryLimit = 100000)
public class RenewalDataElastic implements ElasticDocument, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String entityId;
    private String userId;
    private Long originSeasonTicketId;
    private Long seasonTicketId;
    private String memberId;
    private String productClientId;
    private String email;
    private String name;
    private String surname;
    private String entityName;
    private String birthday;
    private String phoneNumber;
    private String seasonTicketName;
    private String gender;
    private String language;
    private String country;
    private String province;
    private String city;
    private String postalCode;
    private String address;
    private String idCard;
    private String idCardType;
    private ZonedDateTime signUpDate;
    private SeatRenewalES historicSeat;
    private Long historicRateId;
    private String historicRate;
    private SeatRenewalES actualSeat;
    private Long actualRateId;
    private String actualRate;
    private Double balance;
    private Boolean relocation;
    private MappingStatusES mappingStatus;
    private RenewalStatusES renewalStatus;
    private String orderCode;
    private String renewalSubstatus;
    private String iban;
    private String bic;
    private Boolean autoRenewal;

    @Override
    public String getId() {
        return this.id;
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

    public Long getOriginSeasonTicketId() {
        return originSeasonTicketId;
    }

    public void setOriginSeasonTicketId(Long originSeasonTicketId) {
        this.originSeasonTicketId = originSeasonTicketId;
    }

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
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

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public SeatRenewalES getHistoricSeat() {
        return historicSeat;
    }

    public void setHistoricSeat(SeatRenewalES historicSeat) {
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

    public SeatRenewalES getActualSeat() {
        return actualSeat;
    }

    public void setActualSeat(SeatRenewalES actualSeat) {
        this.actualSeat = actualSeat;
    }

    public Long getActualRateId() {
        return actualRateId;
    }

    public void setActualRateId(Long actualRateId) {
        this.actualRateId = actualRateId;
    }

    public String getActualRate() {
        return actualRate;
    }

    public void setActualRate(String actualRate) {
        this.actualRate = actualRate;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public MappingStatusES getMappingStatus() {
        return mappingStatus;
    }

    public void setMappingStatus(MappingStatusES mappingStatus) {
        this.mappingStatus = mappingStatus;
    }

    public RenewalStatusES getRenewalStatus() {
        return renewalStatus;
    }

    public void setRenewalStatus(RenewalStatusES renewalStatus) {
        this.renewalStatus = renewalStatus;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public ZonedDateTime getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(ZonedDateTime signUpDate) {
        this.signUpDate = signUpDate;
    }

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

    public String getRenewalSubstatus() { return renewalSubstatus; }

    public void setRenewalSubstatus(String renewalSubstatus) { this.renewalSubstatus = renewalSubstatus; }

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

    public Boolean getAutoRenewal() { return autoRenewal; }

    public void setAutoRenewal(Boolean autoRenewal) { this.autoRenewal = autoRenewal; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
