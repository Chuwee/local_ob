package es.onebox.event.events.dao.record;

import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

public class EventRecord extends CpanelEventoRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String entityName;
    private String promoterName;
    private Byte useSimplifiedInvoice;
    private String tourName;
    private Integer operatorId;

    //Join by evento.fechainicio
    private String startDateTZ;
    private String startDateTZDesc;
    private Integer startDateTZOffset;

    //Join by evento.fechafin
    private String endDateTZ;
    private String endDateTZDesc;
    private Integer endDateTZOffset;

    //Join by evento.idtaxonomia
    private String categoryDescription;
    private String categoryCode;

    //Join by evento.idtaxonomiapropia
    private String customCategoryDescription;
    private String customCategoryRef;

    //Join by configrecinto.idevento - recinto.idconfiguracion
    private Integer venueId;
    private String venueName;
    private Integer venueCountryId;
    private Integer venueCountrySubdivisionId;
    private String googlePlaceId;

    private Boolean isMemberMandatory;
    private Boolean allowRenewal;
    private Boolean renewalEnabled;
    private Timestamp renewalStartingDate;
    private Timestamp renewalEndDate;
    private Boolean autoRenewal;

    private Boolean allowChangeSeat;
    private Boolean changeSeatEnabled;
    private Timestamp changeSeatStartingDate;
    private Timestamp changeSeatEndDate;
    private Boolean enableChangedSeatQuota;
    private Integer changedSeatQuotaId;
    private Integer changedSeatStatus;
    private Integer changedSeatBlockReasonId;
    private Boolean maxChangeSeatValueEnabled;
    private Integer maxChangeSeatValue;
    private Boolean limitChangedSeatQuotas;
    private Boolean allowTransferTicket;
    private Integer transferTicketMaxDelayTime;
    private Integer recoveryTicketMaxDelayTime;
    private Boolean enableMaxTicketTransfers;
    private Integer maxTicketTransfers;
    private Integer currencyId;
    private Boolean allowReleaseSeat;
    private Boolean maxReleasesEnabled;
    private Integer transferTicketMinDelayTime;
    private Byte sessionTransferPolicy;
    private Boolean registerMandatory;
    private Integer customerMaxSeats;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getPromoterName() {
        return promoterName;
    }

    public void setPromoterName(String promoterName) {
        this.promoterName = promoterName;
    }

    public Byte getUseSimplifiedInvoice() {
        return useSimplifiedInvoice;
    }

    public void setUseSimplifiedInvoice(Byte useSimplifiedInvoice) {
        this.useSimplifiedInvoice = useSimplifiedInvoice;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getStartDateTZ() {
        return startDateTZ;
    }

    public void setStartDateTZ(String startDateTZ) {
        this.startDateTZ = startDateTZ;
    }

    public String getStartDateTZDesc() {
        return startDateTZDesc;
    }

    public void setStartDateTZDesc(String startDateTZDesc) {
        this.startDateTZDesc = startDateTZDesc;
    }

    public Integer getStartDateTZOffset() {
        return startDateTZOffset;
    }

    public void setStartDateTZOffset(Integer startDateTZOffset) {
        this.startDateTZOffset = startDateTZOffset;
    }

    public String getEndDateTZ() {
        return endDateTZ;
    }

    public void setEndDateTZ(String endDateTZ) {
        this.endDateTZ = endDateTZ;
    }

    public String getEndDateTZDesc() {
        return endDateTZDesc;
    }

    public void setEndDateTZDesc(String endDateTZDesc) {
        this.endDateTZDesc = endDateTZDesc;
    }

    public Integer getEndDateTZOffset() {
        return endDateTZOffset;
    }

    public void setEndDateTZOffset(Integer endDateTZOffset) {
        this.endDateTZOffset = endDateTZOffset;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCustomCategoryDescription() {
        return customCategoryDescription;
    }

    public void setCustomCategoryDescription(String customCategoryDescription) {
        this.customCategoryDescription = customCategoryDescription;
    }

    public String getCustomCategoryRef() {
        return customCategoryRef;
    }

    public void setCustomCategoryRef(String customCategoryRef) {
        this.customCategoryRef = customCategoryRef;
    }

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public Integer getVenueCountryId() {
        return venueCountryId;
    }

    public void setVenueCountryId(Integer venueCountryId) {
        this.venueCountryId = venueCountryId;
    }

    public Integer getVenueCountrySubdivisionId() {
        return venueCountrySubdivisionId;
    }

    public void setVenueCountrySubdivisionId(Integer venueCountrySubdivisionId) {
        this.venueCountrySubdivisionId = venueCountrySubdivisionId;
    }

    public String getGooglePlaceId() { return googlePlaceId; }

    public void setGooglePlaceId(String googlePlaceId) { this.googlePlaceId = googlePlaceId; }

    public Boolean getMemberMandatory() {
        return isMemberMandatory;
    }

    public void setMemberMandatory(Boolean memberMandatory) {
        isMemberMandatory = memberMandatory;
    }


    public Boolean getAllowRenewal() {
        return allowRenewal;
    }

    public void setAllowRenewal(Boolean allowRenewal) {
        this.allowRenewal = allowRenewal;
    }

    public Boolean getRenewalEnabled() {
        return renewalEnabled;
    }

    public void setRenewalEnabled(Boolean renewalEnabled) {
        this.renewalEnabled = renewalEnabled;
    }

    public Timestamp getRenewalStartingDate() {
        return renewalStartingDate;
    }

    public void setRenewalStartingDate(Timestamp renewalStartingDate) {
        this.renewalStartingDate = renewalStartingDate;
    }

    public Timestamp getRenewalEndDate() {
        return renewalEndDate;
    }

    public void setRenewalEndDate(Timestamp renewalEndDate) {
        this.renewalEndDate = renewalEndDate;
    }

    public Boolean getAllowChangeSeat() {
        return allowChangeSeat;
    }

    public void setAllowChangeSeat(Boolean allowChangeSeat) {
        this.allowChangeSeat = allowChangeSeat;
    }

    public Boolean getChangeSeatEnabled() {
        return changeSeatEnabled;
    }

    public void setChangeSeatEnabled(Boolean changeSeatEnabled) {
        this.changeSeatEnabled = changeSeatEnabled;
    }

    public Timestamp getChangeSeatStartingDate() {
        return changeSeatStartingDate;
    }

    public void setChangeSeatStartingDate(Timestamp changeSeatStartingDate) {
        this.changeSeatStartingDate = changeSeatStartingDate;
    }

    public Timestamp getChangeSeatEndDate() {
        return changeSeatEndDate;
    }

    public void setChangeSeatEndDate(Timestamp changeSeatEndDate) {
        this.changeSeatEndDate = changeSeatEndDate;
    }

    public Boolean getMaxChangeSeatValueEnabled() {
        return maxChangeSeatValueEnabled;
    }

    public void setMaxChangeSeatValueEnabled(Boolean maxChangeSeatValueEnabled) {
        this.maxChangeSeatValueEnabled = maxChangeSeatValueEnabled;
    }

    public Integer getMaxChangeSeatValue() {
        return maxChangeSeatValue;
    }

    public void setMaxChangeSeatValue(Integer maxChangeSeatValue) {
        this.maxChangeSeatValue = maxChangeSeatValue;
    }

    public Boolean getEnableChangedSeatQuota() {
        return enableChangedSeatQuota;
    }

    public void setEnableChangedSeatQuota(Boolean enableChangedSeatQuota) {
        this.enableChangedSeatQuota = enableChangedSeatQuota;
    }

    public Integer getChangedSeatQuotaId() {
        return changedSeatQuotaId;
    }

    public void setChangedSeatQuotaId(Integer changedSeatQuotaId) {
        this.changedSeatQuotaId = changedSeatQuotaId;
    }

    public Integer getChangedSeatStatus() {
        return changedSeatStatus;
    }

    public void setChangedSeatStatus(Integer changedSeatStatus) {
        this.changedSeatStatus = changedSeatStatus;
    }

    public Integer getChangedSeatBlockReasonId() {
        return changedSeatBlockReasonId;
    }

    public void setChangedSeatBlockReasonId(Integer changedSeatBlockReasonId) {
        this.changedSeatBlockReasonId = changedSeatBlockReasonId;
    }

    public Boolean getLimitChangedSeatQuotas() {
        return limitChangedSeatQuotas;
    }

    public void setLimitChangedSeatQuotas(Boolean limitChangedSeatQuotas) {
        this.limitChangedSeatQuotas = limitChangedSeatQuotas;
    }

    public Boolean getAllowTransferTicket() {
        return allowTransferTicket;
    }

    public void setAllowTransferTicket(Boolean allowTransferTicket) {
        this.allowTransferTicket = allowTransferTicket;
    }

    public Integer getTransferTicketMaxDelayTime() {
        return transferTicketMaxDelayTime;
    }

    public void setTransferTicketMaxDelayTime(Integer transferTicketMaxDelayTime) {
        this.transferTicketMaxDelayTime = transferTicketMaxDelayTime;
    }

    public Integer getRecoveryTicketMaxDelayTime() {
        return recoveryTicketMaxDelayTime;
    }

    public void setRecoveryTicketMaxDelayTime(Integer recoveryTicketMaxDelayTime) {
        this.recoveryTicketMaxDelayTime = recoveryTicketMaxDelayTime;
    }

    public Boolean getEnableMaxTicketTransfers() {
        return enableMaxTicketTransfers;
    }

    public void setEnableMaxTicketTransfers(Boolean enableMaxTicketTransfers) {
        this.enableMaxTicketTransfers = enableMaxTicketTransfers;
    }

    public Integer getCurrencyId() { return currencyId; }

    public void setCurrencyId(Integer currencyId) { this.currencyId = currencyId; }

    public Integer getMaxTicketTransfers() {
        return maxTicketTransfers;
    }

    public void setMaxTicketTransfers(Integer maxTicketTransfers) {
        this.maxTicketTransfers = maxTicketTransfers;
    }

    public Boolean getAllowReleaseSeat() {
        return allowReleaseSeat;
    }

    public void setAllowReleaseSeat(Boolean allowReleaseSeat) {
        this.allowReleaseSeat = allowReleaseSeat;
    }

    public Boolean getMaxReleaseEnabled() {
        return maxReleasesEnabled;
    }

    public void setMaxReleaseEnabled(Boolean maxReleasesEnabled) {
        this.maxReleasesEnabled = maxReleasesEnabled;
    }

    public Integer getTransferTicketMinDelayTime() { return transferTicketMinDelayTime; }

    public void setTransferTicketMinDelayTime(Integer transferTicketMinDelayTime) {
        this.transferTicketMinDelayTime = transferTicketMinDelayTime;
    }

    public Byte getSessionTransferPolicy() {
        return sessionTransferPolicy;
    }

    public void setSessionTransferPolicy(Byte sessionTransferPolicy) {
        this.sessionTransferPolicy = sessionTransferPolicy;
    }

    public Boolean getRegisterMandatory() {
        return registerMandatory;
    }

    public void setRegisterMandatory(Boolean registerMandatory) {
        this.registerMandatory = registerMandatory;
    }

    public Integer getCustomerMaxSeats() {
        return customerMaxSeats;
    }

    public void setCustomerMaxSeats(Integer customerMaxSeats) {
        this.customerMaxSeats = customerMaxSeats;
    }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
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
