package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketRenewalDTO;
import es.onebox.mgmt.seasontickets.enums.SeatMappingStatus;
import es.onebox.mgmt.seasontickets.enums.SeatRenewalStatus;

import java.io.Serializable;

public class SeasonTicketRenewalSeatDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("member_id")
    private String memberId;
    @JsonProperty("product_client_id")
    private String productClientId;
    private String email;
    private String name;
    private String surname;
    private String birthday;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("season_ticket_id")
    private Integer seasonTicketId;
    @JsonProperty("season_ticket_name")
    private String seasonTicketName;
    @JsonProperty("postal_code")
    private String postalCode;
    private String address;
    @JsonProperty("entity_id")
    private Long entityId;
    @JsonProperty("entity_name")
    private String entityName;
    @JsonProperty("historic_seat")
    private SeatRenewalDTO historicSeat;
    @JsonProperty("historic_rate")
    private String historicRate;
    @JsonProperty("historic_rate_id")
    private Integer historicRateId;
    @JsonProperty("actual_seat")
    private SeatRenewalDTO actualSeat;
    @JsonProperty("actual_rate")
    private String actualRate;
    @JsonProperty("actual_rate_id")
    private Integer actualRateId;
    @JsonProperty("mapping_status")
    private SeatMappingStatus mappingStatus;
    @JsonProperty("renewal_status")
    private SeatRenewalStatus renewalStatus;
    @JsonProperty("renewal_settings")
    private SeasonTicketRenewalDTO renewalSettings;
    @JsonProperty("balance")
    private Double balance;
    @JsonProperty("order_code")
    private String orderCode;
    @JsonProperty("auto_renewal")
    private Boolean autoRenewal;
    @JsonProperty("renewal_substatus")
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Integer seasonTicketId) {
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

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public SeatRenewalDTO getHistoricSeat() {
        return historicSeat;
    }

    public void setHistoricSeat(SeatRenewalDTO historicSeat) {
        this.historicSeat = historicSeat;
    }

    public String getHistoricRate() {
        return historicRate;
    }

    public void setHistoricRate(String historicRate) {
        this.historicRate = historicRate;
    }

    public Integer getHistoricRateId() {
        return historicRateId;
    }

    public void setHistoricRateId(Integer historicRateId) {
        this.historicRateId = historicRateId;
    }

    public SeatRenewalDTO getActualSeat() {
        return actualSeat;
    }

    public void setActualSeat(SeatRenewalDTO actualSeat) {
        this.actualSeat = actualSeat;
    }

    public String getActualRate() {
        return actualRate;
    }

    public void setActualRate(String actualRate) {
        this.actualRate = actualRate;
    }

    public Integer getActualRateId() {
        return actualRateId;
    }

    public void setActualRateId(Integer actualRateId) {
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

    public SeasonTicketRenewalDTO getRenewalSettings() {
        return renewalSettings;
    }

    public void setRenewalSettings(SeasonTicketRenewalDTO renewalSettings) {
        this.renewalSettings = renewalSettings;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
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

    public String getRenewalSubstatus() {
        return renewalSubstatus;
    }

    public void setRenewalSubstatus(String renewalSubstatus) {
        this.renewalSubstatus = renewalSubstatus;
    }
}
