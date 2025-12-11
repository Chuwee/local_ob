package es.onebox.event.report.model.report;

import es.onebox.core.file.exporter.generator.model.ExportableBean;
import es.onebox.event.report.annotation.SeasonTicketRenewalFieldBinder;
import es.onebox.event.report.enums.SeasonTicketRenewalsField;


import java.time.LocalDate;
import java.time.ZonedDateTime;

public class SeasonTicketRenewalsReportDTO implements ExportableBean {

    private static final long serialVersionUID = 3943186760269302612L;

    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ID)
    private String id;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.USER_ID)
    private String userId;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.MEMBER_ID)
    private String memberId;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.PRODUCT_CLIENT_ID)
    private String productClientId;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.EMAIL)
    private String email;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.NAME)
    private String name;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.SURNAME)
    private String surname;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.BIRTHDAY)
    private String birthday;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.PHONE_NUMBER)
    private String phoneNumber;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.SEASON_TICKET_ID)
    private Long seasonTicketId;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.SEASON_TICKET_NAME)
    private String seasonTicketName;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.POSTAL_CODE)
    private String postalCode;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.GENDER)
    private String gender;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.LANGUAGE)
    private String language;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.COUNTRY)
    private String country;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.COUNTRY_SUBDIVISION)
    private String countrySubdivision;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.CITY)
    private String city;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ID_CARD)
    private String idCard;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.SIGN_UP_DATE)
    private ZonedDateTime signUpDate;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ADDRESS)
    private String address;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ENTITY_ID)
    private Long entityId;

    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.HISTORIC_SEAT_TYPE)
    private String historicSeatType;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.HISTORIC_SEAT_NOT_NUMBERED_ZONE_ID)
    private Long historicSeatNotNumberedZoneId;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.HISTORIC_SEAT_SECTOR_ID)
    private Long historicSeatSectorId;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.HISTORIC_SEAT_ROW_ID)
    private Long historicSeatRowId;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.HISTORIC_SEAT_SEAT_ID)
    private Long historicSeatSeatId;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.HISTORIC_SEAT_SECTOR)
    private String historicSeatSector;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.HISTORIC_SEAT_ROW)
    private String historicSeatRow;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.HISTORIC_SEAT_SEAT)
    private String historicSeatSeat;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.HISTORIC_SEAT_PRICE_ZONE)
    private String historicSeatPriceZone;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.HISTORIC_SEAT_NOT_NUMBERED_ZONE)
    private String historicSeatNotNumberedZone;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.HISTORIC_RATE)
    private String historicRate;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.HISTORIC_RATE_ID)
    private Long historicRateId;

    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ACTUAL_SEAT_TYPE)
    private String actualSeatType;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ACTUAL_SEAT_NOT_NUMBERED_ZONE_ID)
    private Long actualSeatNotNumberedZoneId;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ACTUAL_SEAT_SECTOR_ID)
    private Long actualSeatSectorId;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ACTUAL_SEAT_ROW_ID)
    private Long actualSeatRowId;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ACTUAL_SEAT_SEAT_ID)
    private Long actualSeatSeatId;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ACTUAL_SEAT_SECTOR)
    private String actualSeatSector;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ACTUAL_SEAT_ROW)
    private String actualSeatRow;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ACTUAL_SEAT_SEAT)
    private String actualSeatSeat;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ACTUAL_SEAT_PRICE_ZONE)
    private String actualSeatPriceZone;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ACTUAL_SEAT_NOT_NUMBERED_ZONE)
    private String actualSeatNotNumberedZone;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ACTUAL_RATE)
    private String actualRate;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ACTUAL_RATE_ID)
    private Long actualRateId;

    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.MAPPING_STATUS)
    private String mappingStatus;

    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.RENEWAL_STATUS)
    private String renewalStatus;

    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.RENEWALS_SETTINGS_ENABLE)
    private Boolean renewalsSettingsEnable;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.RENEWALS_SETTINGS_START_DATE)
    private LocalDate renewalsSettingsStartDate;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.RENEWALS_SETTINGS_END_DATE)
    private LocalDate renewalsSettingsEndDate;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.RENEWALS_SETTINGS_IN_PROCESS)
    private Boolean renewalsSettingsInProcess;
    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.RENEWALS_SETTINGS_AUTORENEWAL)
    private Boolean renewalsSettingsAutoRenewal;

    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.BALANCE)
    private Double balance;

    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.ORDER_CODE)
    private String orderCode;

    @SeasonTicketRenewalFieldBinder(SeasonTicketRenewalsField.AUTO_RENEWAL)
    private Boolean autoRenewal;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getHistoricSeatType() {
        return historicSeatType;
    }

    public void setHistoricSeatType(String historicSeatType) {
        this.historicSeatType = historicSeatType;
    }

    public Long getHistoricSeatNotNumberedZoneId() {
        return historicSeatNotNumberedZoneId;
    }

    public void setHistoricSeatNotNumberedZoneId(Long historicSeatNotNumberedZoneId) {
        this.historicSeatNotNumberedZoneId = historicSeatNotNumberedZoneId;
    }

    public Long getHistoricSeatSectorId() {
        return historicSeatSectorId;
    }

    public void setHistoricSeatSectorId(Long historicSeatSectorId) {
        this.historicSeatSectorId = historicSeatSectorId;
    }

    public Long getHistoricSeatRowId() {
        return historicSeatRowId;
    }

    public void setHistoricSeatRowId(Long historicSeatRowId) {
        this.historicSeatRowId = historicSeatRowId;
    }

    public Long getHistoricSeatSeatId() {
        return historicSeatSeatId;
    }

    public void setHistoricSeatSeatId(Long historicSeatSeatId) {
        this.historicSeatSeatId = historicSeatSeatId;
    }

    public String getHistoricSeatSector() {
        return historicSeatSector;
    }

    public void setHistoricSeatSector(String historicSeatSector) {
        this.historicSeatSector = historicSeatSector;
    }

    public String getHistoricSeatRow() {
        return historicSeatRow;
    }

    public void setHistoricSeatRow(String historicSeatRow) {
        this.historicSeatRow = historicSeatRow;
    }

    public String getHistoricSeatSeat() {
        return historicSeatSeat;
    }

    public void setHistoricSeatSeat(String historicSeatSeat) {
        this.historicSeatSeat = historicSeatSeat;
    }

    public String getHistoricSeatPriceZone() {
        return historicSeatPriceZone;
    }

    public void setHistoricSeatPriceZone(String historicSeatPriceZone) {
        this.historicSeatPriceZone = historicSeatPriceZone;
    }

    public String getHistoricSeatNotNumberedZone() {
        return historicSeatNotNumberedZone;
    }

    public void setHistoricSeatNotNumberedZone(String historicSeatNotNumberedZone) {
        this.historicSeatNotNumberedZone = historicSeatNotNumberedZone;
    }

    public String getHistoricRate() {
        return historicRate;
    }

    public void setHistoricRate(String historicRate) {
        this.historicRate = historicRate;
    }

    public Long getHistoricRateId() {
        return historicRateId;
    }

    public void setHistoricRateId(Long historicRateId) {
        this.historicRateId = historicRateId;
    }

    public String getActualSeatType() {
        return actualSeatType;
    }

    public void setActualSeatType(String actualSeatType) {
        this.actualSeatType = actualSeatType;
    }

    public Long getActualSeatNotNumberedZoneId() {
        return actualSeatNotNumberedZoneId;
    }

    public void setActualSeatNotNumberedZoneId(Long actualSeatNotNumberedZoneId) {
        this.actualSeatNotNumberedZoneId = actualSeatNotNumberedZoneId;
    }

    public Long getActualSeatSectorId() {
        return actualSeatSectorId;
    }

    public void setActualSeatSectorId(Long actualSeatSectorId) {
        this.actualSeatSectorId = actualSeatSectorId;
    }

    public Long getActualSeatRowId() {
        return actualSeatRowId;
    }

    public void setActualSeatRowId(Long actualSeatRowId) {
        this.actualSeatRowId = actualSeatRowId;
    }

    public Long getActualSeatSeatId() {
        return actualSeatSeatId;
    }

    public void setActualSeatSeatId(Long actualSeatSeatId) {
        this.actualSeatSeatId = actualSeatSeatId;
    }

    public String getActualSeatSector() {
        return actualSeatSector;
    }

    public void setActualSeatSector(String actualSeatSector) {
        this.actualSeatSector = actualSeatSector;
    }

    public String getActualSeatRow() {
        return actualSeatRow;
    }

    public void setActualSeatRow(String actualSeatRow) {
        this.actualSeatRow = actualSeatRow;
    }

    public String getActualSeatSeat() {
        return actualSeatSeat;
    }

    public void setActualSeatSeat(String actualSeatSeat) {
        this.actualSeatSeat = actualSeatSeat;
    }

    public String getActualSeatPriceZone() {
        return actualSeatPriceZone;
    }

    public void setActualSeatPriceZone(String actualSeatPriceZone) {
        this.actualSeatPriceZone = actualSeatPriceZone;
    }

    public String getActualSeatNotNumberedZone() {
        return actualSeatNotNumberedZone;
    }

    public void setActualSeatNotNumberedZone(String actualSeatNotNumberedZone) {
        this.actualSeatNotNumberedZone = actualSeatNotNumberedZone;
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

    public String getMappingStatus() {
        return mappingStatus;
    }

    public void setMappingStatus(String mappingStatus) {
        this.mappingStatus = mappingStatus;
    }

    public String getRenewalStatus() {
        return renewalStatus;
    }

    public void setRenewalStatus(String renewalStatus) {
        this.renewalStatus = renewalStatus;
    }

    public Boolean getRenewalsSettingsEnable() {
        return renewalsSettingsEnable;
    }

    public void setRenewalsSettingsEnable(Boolean renewalsSettingsEnable) {
        this.renewalsSettingsEnable = renewalsSettingsEnable;
    }

    public LocalDate getRenewalsSettingsStartDate() {
        return renewalsSettingsStartDate;
    }

    public void setRenewalsSettingsStartDate(LocalDate renewalsSettingsStartDate) {
        this.renewalsSettingsStartDate = renewalsSettingsStartDate;
    }

    public LocalDate getRenewalsSettingsEndDate() {
        return renewalsSettingsEndDate;
    }

    public void setRenewalsSettingsEndDate(LocalDate renewalsSettingsEndDate) {
        this.renewalsSettingsEndDate = renewalsSettingsEndDate;
    }

    public Boolean getRenewalsSettingsInProcess() {
        return renewalsSettingsInProcess;
    }

    public void setRenewalsSettingsInProcess(Boolean renewalsSettingsInProcess) {
        this.renewalsSettingsInProcess = renewalsSettingsInProcess;
    }
    public Double getBalance() { return balance; }

    public void setBalance(Double balance) { this.balance = balance; }

    public Boolean getRenewalsSettingsAutoRenewal() {
        return renewalsSettingsAutoRenewal;
    }

    public void setRenewalsSettingsAutoRenewal(Boolean renewalsSettingsAutoRenewal) {
        this.renewalsSettingsAutoRenewal = renewalsSettingsAutoRenewal;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }
}
