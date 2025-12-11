package es.onebox.event.sessions.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class PreSaleConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean active;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private List<Integer> activeChannels;
    private Integer memberTicketsLimit;
    private Integer generalTicketsLimit;
    private Integer presalePromotionId;
    private Boolean multiplePurchase;

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

    public Boolean getMultiplePurchase() {
        return multiplePurchase;
    }

    public void setMultiplePurchase(Boolean multiplePurchase) {
        this.multiplePurchase = multiplePurchase;
    }
}
