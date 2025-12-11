package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.enums.AvetPermission;
import es.onebox.mgmt.datasources.ms.entity.enums.MemberPeriodType;
import io.micrometer.core.lang.NonNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class UpdateMemberConfigsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NonNull
    @JsonProperty("free_seat")
    private Boolean freeSeat;

    @NonNull
    @JsonProperty("member_enabled")
    private Boolean memberEnabled;

    @NonNull
    @JsonProperty("max_additional_members")
    private Integer maxAdditionalMembers;

    @NonNull
    @JsonProperty("payment_renew_partner")
    private Long paymentRenewPartner;

    @NonNull
    @JsonProperty("emision_renew_partner")
    private Long emisionRenewPartner;

    @JsonProperty("member_operation_periods")
    private Map<MemberPeriodType, MemberOperationPeriodDTO> memberOperationPeriods;

    @JsonProperty("change_pin")
    private Boolean changePin;

    @JsonProperty("remember_pin")
    private Boolean rememberPin;

    @JsonProperty("blocked_matches")
    private List<Integer> blockedMatches;

    @JsonProperty("allow_free_seat_till")
    private Long allowFreeSeatTill;

    @JsonProperty("allow_recover_seat_till")
    private Long allowRecoverSeatTill;

    @JsonProperty("user_area")
    private Boolean userArea;

    @JsonProperty("transfer_seat")
    private Boolean transferSeat;

    @JsonProperty("show_role")
    private Boolean showRole;

    @JsonProperty("show_subscription_mode")
    private Boolean showSubscriptionMode;

    @JsonProperty("show_previous_seat")
    private Boolean showPreviousSeat;

    @JsonProperty("force_regenerate_passbook")
    private Boolean forceRegeneratePassbook;

    @JsonProperty("expiration_date_passbook")
    private ZonedDateTime expirationDatePassbook;

    @JsonProperty("open_additional_members")
    private Boolean openAdditionalMembers;

    @JsonProperty("signup_email")
    private Boolean signUpEmail;

    @JsonProperty("buy_url")
    private String buyUrl;

    @JsonProperty("captcha_site_key")
    private String captchaSiteKey;

    @JsonProperty("captcha_enabled")
    private Boolean captchaEnabled;

    @JsonProperty("captcha_secret_key")
    private String captchaSecretKey;

    @JsonProperty("public_availability_enabled")
    private Boolean publicAvailabilityEnabled;

    @JsonProperty("landing_button_url")
    private String landingButtonUrl;

    @JsonProperty("download_passbook_permissions")
    private List<AvetPermission> downloadPassbookPermissions;

    @JsonProperty("new_member_permission")
    private AvetPermission newMemberPermission;

    @JsonProperty("buy_seat_permission")
    private AvetPermission buySeatPermission;

    @JsonProperty("allow_cross_purchases")
    private Boolean allowCrossPurchases;

    @JsonProperty("allow_tutor_form")
    private Boolean allowTutorForm;

    @JsonProperty("tutee_max_age")
    private Integer tuteeMaxAge;

    @JsonProperty("members_card_image")
    private MembersCardImageContentDTO membersCardImageContent;

    public Boolean getFreeSeat() {
        return freeSeat;
    }

    public void setFreeSeat(Boolean freeSeat) {
        this.freeSeat = freeSeat;
    }

    public Boolean getMemberEnabled() {
        return memberEnabled;
    }

    public void setMemberEnabled(Boolean memberEnabled) {
        this.memberEnabled = memberEnabled;
    }

    public Integer getMaxAdditionalMembers() {
        return maxAdditionalMembers;
    }

    public void setMaxAdditionalMembers(Integer maxAdditionalMembers) {
        this.maxAdditionalMembers = maxAdditionalMembers;
    }

    public Long getPaymentRenewPartner() {
        return paymentRenewPartner;
    }

    public void setPaymentRenewPartner(Long paymentRenewPartner) {
        this.paymentRenewPartner = paymentRenewPartner;
    }

    public Long getEmisionRenewPartner() {
        return emisionRenewPartner;
    }

    public void setEmisionRenewPartner(Long emisionRenewPartner) {
        this.emisionRenewPartner = emisionRenewPartner;
    }

    public Map<MemberPeriodType, MemberOperationPeriodDTO> getMemberOperationPeriods() {
        return memberOperationPeriods;
    }

    public void setMemberOperationPeriods(Map<MemberPeriodType, MemberOperationPeriodDTO> memberOperationPeriods) {
        this.memberOperationPeriods = memberOperationPeriods;
    }

    public Boolean getChangePin() {
        return changePin;
    }

    public void setChangePin(Boolean changePin) {
        this.changePin = changePin;
    }

    public Boolean getRememberPin() {
        return rememberPin;
    }

    public void setRememberPin(Boolean rememberPin) {
        this.rememberPin = rememberPin;
    }

    public List<Integer> getBlockedMatches() {
        return blockedMatches;
    }

    public void setBlockedMatches(List<Integer> blockedMatches) {
        this.blockedMatches = blockedMatches;
    }

    public Long getAllowFreeSeatTill() {
        return allowFreeSeatTill;
    }

    public void setAllowFreeSeatTill(Long allowFreeSeatTill) {
        this.allowFreeSeatTill = allowFreeSeatTill;
    }

    public Long getAllowRecoverSeatTill() {
        return allowRecoverSeatTill;
    }

    public void setAllowRecoverSeatTill(Long allowRecoverSeatTill) {
        this.allowRecoverSeatTill = allowRecoverSeatTill;
    }

    public Boolean getUserArea() {
        return userArea;
    }

    public void setUserArea(Boolean userArea) {
        this.userArea = userArea;
    }

    public Boolean getTransferSeat() {
        return transferSeat;
    }

    public void setTransferSeat(Boolean transferSeat) {
        this.transferSeat = transferSeat;
    }

    public Boolean getShowRole() {
        return showRole;
    }

    public void setShowRole(Boolean showRole) {
        this.showRole = showRole;
    }

    public Boolean getShowSubscriptionMode() {
        return showSubscriptionMode;
    }

    public void setShowSubscriptionMode(Boolean showSubscriptionMode) {
        this.showSubscriptionMode = showSubscriptionMode;
    }

    public Boolean getShowPreviousSeat() {
        return showPreviousSeat;
    }

    public void setShowPreviousSeat(Boolean showPreviousSeat) {
        this.showPreviousSeat = showPreviousSeat;
    }

    public Boolean getForceRegeneratePassbook() {
        return forceRegeneratePassbook;
    }

    public void setForceRegeneratePassbook(Boolean forceRegeneratePassbook) {
        this.forceRegeneratePassbook = forceRegeneratePassbook;
    }

    public ZonedDateTime getExpirationDatePassbook() {
        return expirationDatePassbook;
    }

    public void setExpirationDatePassbook(ZonedDateTime expirationDatePassbook) {
        this.expirationDatePassbook = expirationDatePassbook;
    }

    public Boolean getOpenAdditionalMembers() {
        return openAdditionalMembers;
    }

    public void setOpenAdditionalMembers(Boolean openAdditionalMembers) {
        this.openAdditionalMembers = openAdditionalMembers;
    }

    public Boolean getSignUpEmail() {
        return signUpEmail;
    }

    public void setSignUpEmail(Boolean signUpEmail) {
        this.signUpEmail = signUpEmail;
    }

    public String getBuyUrl() {
        return buyUrl;
    }

    public void setBuyUrl(String buyUrl) {
        this.buyUrl = buyUrl;
    }

    public Boolean getCaptchaEnabled() {
        return captchaEnabled;
    }

    public void setCaptchaEnabled(Boolean captchaEnabled) {
        this.captchaEnabled = captchaEnabled;
    }

    public String getCaptchaSiteKey() {
        return captchaSiteKey;
    }

    public void setCaptchaSiteKey(String captchaSiteKey) {
        this.captchaSiteKey = captchaSiteKey;
    }

    public String getCaptchaSecretKey() {
        return captchaSecretKey;
    }

    public void setCaptchaSecretKey(String captchaSecretKey) {
        this.captchaSecretKey = captchaSecretKey;
    }

    public Boolean getPublicAvailabilityEnabled() {
        return publicAvailabilityEnabled;
    }

    public void setPublicAvailabilityEnabled(Boolean publicAvailabilityEnabled) {
        this.publicAvailabilityEnabled = publicAvailabilityEnabled;
    }

    public String getLandingButtonUrl() {
        return landingButtonUrl;
    }

    public void setLandingButtonUrl(String landingButtonUrl) {
        this.landingButtonUrl = landingButtonUrl;
    }

    public List<AvetPermission> getDownloadPassbookPermissions() {
        return downloadPassbookPermissions;
    }

    public void setDownloadPassbookPermissions(List<AvetPermission> downloadPassbookPermissions) {
        this.downloadPassbookPermissions = downloadPassbookPermissions;
    }

    public AvetPermission getNewMemberPermission() {
        return newMemberPermission;
    }

    public void setNewMemberPermission(AvetPermission newMemberPermission) {
        this.newMemberPermission = newMemberPermission;
    }

    public AvetPermission getBuySeatPermission() {
        return buySeatPermission;
    }

    public void setBuySeatPermission(AvetPermission buySeatPermission) {
        this.buySeatPermission = buySeatPermission;
    }

    public Boolean getAllowCrossPurchases() {
        return allowCrossPurchases;
    }

    public void setAllowCrossPurchases(Boolean allowCrossPurchases) {
        this.allowCrossPurchases = allowCrossPurchases;
    }

    public Boolean getAllowTutorForm() {
        return allowTutorForm;
    }

    public void setAllowTutorForm(Boolean allowTutorForm) {
        this.allowTutorForm = allowTutorForm;
    }

    public Integer getTuteeMaxAge() {
        return tuteeMaxAge;
    }

    public void setTuteeMaxAge(Integer tuteeMaxAge) {
        this.tuteeMaxAge = tuteeMaxAge;
    }

    public MembersCardImageContentDTO getMembersCardImageContent() {
        return membersCardImageContent;
    }

    public void setMembersCardImageContent(MembersCardImageContentDTO membersCardImageContent) {
        this.membersCardImageContent = membersCardImageContent;
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
