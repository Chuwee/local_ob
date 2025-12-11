
package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.channels.enums.MemberPinStrategy;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.Avatar;
import es.onebox.mgmt.datasources.ms.entity.enums.BuySeatFlow;
import es.onebox.mgmt.datasources.ms.entity.enums.MemberIgnoredSteps;
import es.onebox.mgmt.datasources.ms.entity.enums.NewMemberFlow;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class MemberOperationPeriod implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //Generics
    private Boolean active;
    private List<MemberIgnoredSteps> ignoredSteps;
    private Double charge;
    private Boolean orphanSeatsEnabled;
    private Boolean showUpdatePartnerUser;
    private Boolean skipPeriodicityModule;
    private Boolean showConditions;
    private Long paymentMode;
    private Long emissionReason;
    private Boolean payPeriod;
    private Boolean setComment;
    private Boolean updateStatus;
    private String targetStatus;
    private Boolean datesFilterEnabled;

    //Buy Seat
    private BuySeatFlow buySeatFlow;

    //Change Seat
    private Boolean enableMaxChangeSeat;
    private Integer maxChangeSeat;
    private Boolean showChangeSeatCounter;

    //New Member
    private Integer newMemberId;
    private MemberPinStrategy memberPinStrategy;
    private NewMemberFlow newMemberFlow;
    private Avatar avatar;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<MemberIgnoredSteps> getIgnoredSteps() {
        return ignoredSteps;
    }

    public void setIgnoredSteps(List<MemberIgnoredSteps> ignoredSteps) {
        this.ignoredSteps = ignoredSteps;
    }

    public Double getCharge() {
        return charge;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }

    public Boolean getOrphanSeatsEnabled() {
        return orphanSeatsEnabled;
    }

    public void setOrphanSeatsEnabled(Boolean orphanSeatsEnabled) {
        this.orphanSeatsEnabled = orphanSeatsEnabled;
    }

    public Boolean getShowUpdatePartnerUser() {
        return showUpdatePartnerUser;
    }

    public void setShowUpdatePartnerUser(Boolean showUpdatePartnerUser) {
        this.showUpdatePartnerUser = showUpdatePartnerUser;
    }

    public Boolean getSkipPeriodicityModule() {
        return skipPeriodicityModule;
    }

    public void setSkipPeriodicityModule(Boolean skipPeriodicityModule) {
        this.skipPeriodicityModule = skipPeriodicityModule;
    }

    public Boolean getShowConditions() {
        return showConditions;
    }

    public void setShowConditions(Boolean showConditions) {
        this.showConditions = showConditions;
    }

    public Long getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(Long paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Long getEmissionReason() {
        return emissionReason;
    }

    public void setEmissionReason(Long emissionReason) {
        this.emissionReason = emissionReason;
    }

    public Boolean getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(Boolean payPeriod) {
        this.payPeriod = payPeriod;
    }

    public Boolean getSetComment() {
        return setComment;
    }

    public void setSetComment(Boolean setComment) {
        this.setComment = setComment;
    }

    public Boolean getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(Boolean updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(String targetStatus) {
        this.targetStatus = targetStatus;
    }

    public Boolean getDatesFilterEnabled() { return datesFilterEnabled; }

    public void setDatesFilterEnabled(Boolean datesFilterEnabled) { this.datesFilterEnabled = datesFilterEnabled; }

    public BuySeatFlow getBuySeatFlow() {
        return buySeatFlow;
    }

    public void setBuySeatFlow(BuySeatFlow buySeatFlow) {
        this.buySeatFlow = buySeatFlow;
    }

    public Boolean getEnableMaxChangeSeat() {
        return enableMaxChangeSeat;
    }

    public void setEnableMaxChangeSeat(Boolean enableMaxChangeSeat) {
        this.enableMaxChangeSeat = enableMaxChangeSeat;
    }

    public Integer getMaxChangeSeat() {
        return maxChangeSeat;
    }

    public void setMaxChangeSeat(Integer maxChangeSeat) {
        this.maxChangeSeat = maxChangeSeat;
    }

    public Boolean getShowChangeSeatCounter() {
        return showChangeSeatCounter;
    }

    public void setShowChangeSeatCounter(Boolean showChangeSeatCounter) {
        this.showChangeSeatCounter = showChangeSeatCounter;
    }

    public Integer getNewMemberId() {
        return newMemberId;
    }

    public void setNewMemberId(Integer newMemberId) {
        this.newMemberId = newMemberId;
    }

    public MemberPinStrategy getMemberPinStrategy() {
        return memberPinStrategy;
    }

    public void setMemberPinStrategy(MemberPinStrategy memberPinStrategy) {
        this.memberPinStrategy = memberPinStrategy;
    }
    public NewMemberFlow getNewMemberFlow() {
        return newMemberFlow;
    }

    public void setNewMemberFlow(NewMemberFlow newMemberFlow) {
        this.newMemberFlow = newMemberFlow;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
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
