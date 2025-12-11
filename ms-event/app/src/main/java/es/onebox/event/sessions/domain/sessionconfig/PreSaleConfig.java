package es.onebox.event.sessions.domain.sessionconfig;/**
 * @author cgalindo
 */


import es.onebox.event.sessions.enums.PresaleValidatorType;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class PreSaleConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 4369556772912316227L;

    private Long id;
    private boolean active;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private List<Integer> activeChannels;
    private Long validatorId;
    private PresaleValidatorType validatorType;
    private Integer memberTicketsLimit;
    private Integer generalTicketsLimit;
    private Boolean multiplePurchase;
    private Integer presalePromotionId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public List<Integer> getActiveChannels() {
        return activeChannels;
    }

    public void setActiveChannels(List<Integer> activeChannels) {
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

    public Boolean getMultiplePurchase() {
        return multiplePurchase;
    }

    public void setMultiplePurchase(Boolean multiplePurchase) {
        this.multiplePurchase = multiplePurchase;
    }

    public Integer getPresalePromotionId() {
        return presalePromotionId;
    }

    public void setPresalePromotionId(Integer presalePromotionId) {
        this.presalePromotionId = presalePromotionId;
    }
}
