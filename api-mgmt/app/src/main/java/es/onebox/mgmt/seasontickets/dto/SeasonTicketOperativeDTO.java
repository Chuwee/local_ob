package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketOperativeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8234414864640993160L;

    private SetttingsReleaseDTO release;

    private SettingsBookingsDTO booking;

    private SettingsSalesDTO sale;

    @JsonProperty("secondary_market_sale")
    private SettingsSecondaryMarketDTO secondaryMarket;

    @JsonProperty("max_buying_limit")
    private MaxBuyingLimitDTO maxBuyingLimit;

    @JsonProperty("member_required")
    private Boolean memberMandatory;

    @JsonProperty("allow_renewal")
    private Boolean allowRenewal;

    @JsonProperty("renewal")
    private SeasonTicketRenewalDTO renewal;

    @JsonProperty("allow_change_seat")
    private Boolean allowChangeSeat;

    @JsonProperty("change_seat")
    private SeasonTicketChangeSeatDTO changeSeat;

    @JsonProperty("allow_transfer")
    private Boolean allowTransfer;

    @JsonProperty("allow_release_seat")
    private Boolean allowReleaseSeat;

    @JsonProperty("register_mandatory")
    private Boolean registerMandatory;

    @Min(1)
    @JsonProperty("customer_max_seats")
    private Integer customerMaxSeats;

    public MaxBuyingLimitDTO getMaxBuyingLimit() {
        return maxBuyingLimit;
    }

    public void setMaxBuyingLimit(MaxBuyingLimitDTO maxBuyingLimit) {
        this.maxBuyingLimit = maxBuyingLimit;
    }

    public Boolean getMemberMandatory() {
        return memberMandatory;
    }

    public void setMemberMandatory(Boolean memberMandatory) {
        this.memberMandatory = memberMandatory;
    }

    public Boolean getAllowRenewal() {
        return allowRenewal;
    }

    public void setAllowRenewal(Boolean allowRenewal) {
        this.allowRenewal = allowRenewal;
    }

    public SeasonTicketRenewalDTO getRenewal() {
        return renewal;
    }

    public void setRenewal(SeasonTicketRenewalDTO renewal) {
        this.renewal = renewal;
    }

    public Boolean getAllowChangeSeat() {
        return allowChangeSeat;
    }

    public void setAllowChangeSeat(Boolean allowChangeSeat) {
        this.allowChangeSeat = allowChangeSeat;
    }

    public SeasonTicketChangeSeatDTO getChangeSeat() {
        return changeSeat;
    }

    public void setChangeSeat(SeasonTicketChangeSeatDTO changeSeat) {
        this.changeSeat = changeSeat;
    }

    public SetttingsReleaseDTO getRelease() {
        return release;
    }

    public void setRelease(SetttingsReleaseDTO release) {
        this.release = release;
    }

    public SettingsBookingsDTO getBooking() {
        return booking;
    }

    public void setBooking(SettingsBookingsDTO booking) {
        this.booking = booking;
    }

    public SettingsSalesDTO getSale() {
        return sale;
    }

    public void setSale(SettingsSalesDTO sale) {
        this.sale = sale;
    }

    public SettingsSecondaryMarketDTO getSecondaryMarket() {
        return secondaryMarket;
    }

    public void setSecondaryMarket(SettingsSecondaryMarketDTO secondaryMarket) {
        this.secondaryMarket = secondaryMarket;
    }

    public Boolean getAllowTransfer() {
        return allowTransfer;
    }

    public void setAllowTransfer(Boolean allowTransfer) {
        this.allowTransfer = allowTransfer;
    }

    public Boolean getAllowReleaseSeat() {
        return allowReleaseSeat;
    }

    public void setAllowReleaseSeat(Boolean allowReleaseSeat) {
        this.allowReleaseSeat = allowReleaseSeat;
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
}