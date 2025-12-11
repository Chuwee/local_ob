package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.LimitlessValueDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SessionAvailabilityDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private LimitlessValueDTO total;
    private Long available;
    @JsonProperty("promoter_blocked")
    private Long promoterBlocked;
    private Long kill;
    private Long purchase;
    private Long invitation;
    private Long booking;
    private Long issue;
    @JsonProperty("in_progress")
    private Long inProgress;
    @JsonProperty("session_pack")
    private Long sessionPack;

    public LimitlessValueDTO getTotal() {
        return total;
    }

    public void setTotal(LimitlessValueDTO total) {
        this.total = total;
    }

    public Long getAvailable() {
        return available;
    }

    public void setAvailable(Long available) {
        this.available = available;
    }

    public Long getPromoterBlocked() {
        return promoterBlocked;
    }

    public void setPromoterBlocked(Long promoterBlocked) {
        this.promoterBlocked = promoterBlocked;
    }

    public Long getKill() {
        return kill;
    }

    public void setKill(Long kill) {
        this.kill = kill;
    }

    public Long getPurchase() {
        return purchase;
    }

    public void setPurchase(Long purchase) {
        this.purchase = purchase;
    }

    public Long getInvitation() {
        return invitation;
    }

    public void setInvitation(Long invitation) {
        this.invitation = invitation;
    }

    public Long getBooking() {
        return booking;
    }

    public void setBooking(Long booking) {
        this.booking = booking;
    }

    public Long getIssue() {
        return issue;
    }

    public void setIssue(Long issue) {
        this.issue = issue;
    }

    public Long getInProgress() {
        return inProgress;
    }

    public void setInProgress(Long inProgress) {
        this.inProgress = inProgress;
    }

    public Long getSessionPack() {
        return sessionPack;
    }

    public void setSessionPack(Long sessionPack) {
        this.sessionPack = sessionPack;
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
