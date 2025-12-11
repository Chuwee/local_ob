package es.onebox.mgmt.datasources.ms.event.dto.session;

import es.onebox.mgmt.sessions.enums.PresaleValidationRangeType;
import es.onebox.mgmt.sessions.enums.PresaleValidatorType;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class PreSaleConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Boolean active;
    private String name;
    private PresaleValidationRangeType validationRangeType;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private List<Long> activeChannels;
    private List<Long> activeCustomerTypes;
    private PresaleLoyaltyProgram loyaltyProgram;
    private Long validatorId;
    private PresaleValidatorType validatorType;
    private Boolean memberTicketsLimitEnabled;
    private Integer memberTicketsLimit;
    private Integer generalTicketsLimit;
    private Integer presalePromotionId;
    private Boolean multiplePurchase;
    private String externalId;
    private Long entityId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PresaleValidationRangeType getValidationRangeType() {
        return validationRangeType;
    }

    public void setValidationRangeType(PresaleValidationRangeType validationRangeType) {
        this.validationRangeType = validationRangeType;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public List<Long> getActiveChannels() {
        return activeChannels;
    }

    public void setActiveChannels(List<Long> activeChannels) {
        this.activeChannels = activeChannels;
    }

    public Long getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(Long validatorId) {
        this.validatorId = validatorId;
    }

    public PresaleValidatorType getValidatorType() {
        return validatorType;
    }

    public void setValidatorType(PresaleValidatorType validatorType) {
        this.validatorType = validatorType;
    }

    public Boolean getMemberTicketsLimitEnabled() {
        return memberTicketsLimitEnabled;
    }

    public void setMemberTicketsLimitEnabled(Boolean memberTicketsLimitEnabled) {
        this.memberTicketsLimitEnabled = memberTicketsLimitEnabled;
    }

    public Integer getMemberTicketsLimit() {
        return memberTicketsLimit;
    }

    public void setMemberTicketsLimit(Integer memberTicketsLimit) {
        this.memberTicketsLimit = memberTicketsLimit;
    }

    public Integer getGeneralTicketsLimit() {
        return generalTicketsLimit;
    }

    public void setGeneralTicketsLimit(Integer generalTicketsLimit) {
        this.generalTicketsLimit = generalTicketsLimit;
    }

    public Integer getPresalePromotionId() {
        return presalePromotionId;
    }

    public void setPresalePromotionId(Integer presalePromotionId) {
        this.presalePromotionId = presalePromotionId;
    }

    public List<Long> getActiveCustomerTypes() {
        return activeCustomerTypes;
    }

    public void setActiveCustomerTypes(List<Long> activeCustomerTypes) {
        this.activeCustomerTypes = activeCustomerTypes;
    }

    public PresaleLoyaltyProgram getLoyaltyProgram() {
        return loyaltyProgram;
    }

    public void setLoyaltyProgram(PresaleLoyaltyProgram loyaltyProgram) {
        this.loyaltyProgram = loyaltyProgram;
    }

    public Boolean getMultiplePurchase() {
        return multiplePurchase;
    }

    public void setMultiplePurchase(Boolean multiplePurchase) {
        this.multiplePurchase = multiplePurchase;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
