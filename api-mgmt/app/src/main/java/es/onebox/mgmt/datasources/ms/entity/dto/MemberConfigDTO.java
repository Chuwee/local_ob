package es.onebox.mgmt.datasources.ms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.dto.MembersCardImageContent;
import es.onebox.mgmt.datasources.ms.entity.enums.AdditionalMemberMethod;
import es.onebox.mgmt.datasources.ms.entity.enums.MemberPeriodType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class MemberConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6711693295411659055L;

    private AdminMemberConfig adminOptions;

    private String backgroundUrl;
    private Boolean buyOwnSeat;
    private Boolean showPartnerQR;
    private String buyUrl;
    private String loginUrl;
    private Boolean captchaEnabled;
    private String captchaSiteKey;
    private Boolean publicAvailabilityEnabled;
    private String landingButtonUrl;
    @JsonProperty("changepin")
    private Boolean changePin;
    @JsonProperty("codigoclub")
    private Long codigoClub;
    private Boolean freeSeat;
    private Boolean enableDarkTheme;
    @JsonProperty("longpin")
    private Long longPin;
    private String phone;
    @JsonProperty("rememberpin")
    private Boolean rememberPin;
    @JsonProperty("partnerfromold")
    private Boolean partnerFromOld;
    private Boolean renewPartner;
    private String staticsUrl;
    private String theme;
    private String receiptLogo;
    private String url;
    private Long entityId;
    private Boolean thirdPartyAgreement;
    private Boolean commercialMailingAgreement;
    private Map<MemberPeriodType, MemberOperationPeriod> memberOperationPeriods;
    private List<MemberCapacity> capacities;
    private List<SubscriptionMode> subscriptionModes;
    private List<Periodicity> periodicityTranslations;
    private List<RoleTranslation> roleTranslations;
    private AdditionalMemberMethod additionalMemberMethod;
    private Boolean userArea;
    private Boolean newMember;

    private Boolean openAdditionalMembers;
    private Boolean signUpEmail;
    private SubscriptionModeInferData subscriptionModeInferData;
    private NewPriceCalculatorData newPriceCalculatorData;
    private PreviousPriceCalculatorData previousPriceCalculatorData;
    private ChangeSeatValidatorData changeSeatValidatorData;
    private RenewalValidatorData renewalValidatorData;
    private BuySeatValidatorData buySeatValidatorData;
    private PartnerRolesRelationInferData partnerRolesRelationInferData;
    private DiscountCalculatorData discountCalculatorData;
    private QueueItConfig queueItConfig;

    //for receipt customization
    private Boolean showRole;
    private Boolean showSubscriptionMode;
    private Boolean showPreviousSeat;
    private Boolean showPeriodicity;
    private List<Long> membershipVirtualZoneIds;
    private Boolean memberEnabled;

    private Long channelId;
    private Boolean forceRegeneratePassbook;
    private Boolean transferSeat;

    private ZonedDateTime expirationDatePassbook;

    private List<MemberRestriction> memberRestrictions;
    private List<String> downloadPassbookPermissions;
    private String newMemberPermission;
    private String buySeatPermission;
    private Long membershipTermId;
    private Long membershipPeriodicityId;
    private Boolean allowCrossPurchases;
    private Boolean allowTutorForm;
    private Integer tuteeMaxAge;
    private MembersCardImageContent membersCardImageContent;

    public MemberConfigDTO() {
    }

    public AdminMemberConfig getAdminOptions() {
        return adminOptions;
    }

    public void setAdminOptions(AdminMemberConfig adminOptions) {
        this.adminOptions = adminOptions;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    public Boolean getBuyOwnSeat() {
        return buyOwnSeat;
    }

    public void setBuyOwnSeat(Boolean buyOwnSeat) {
        this.buyOwnSeat = buyOwnSeat;
    }

    public Boolean getShowPartnerQR() {
        return showPartnerQR;
    }

    public void setShowPartnerQR(Boolean showPartnerQR) {
        this.showPartnerQR = showPartnerQR;
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

    public Boolean getChangePin() {
        return changePin;
    }

    public void setChangePin(Boolean changePin) {
        this.changePin = changePin;
    }

    public Long getCodigoClub() {
        return codigoClub;
    }

    public void setCodigoClub(Long codigoClub) {
        this.codigoClub = codigoClub;
    }

    public Boolean getFreeSeat() {
        return freeSeat;
    }

    public void setFreeSeat(Boolean freeSeat) {
        this.freeSeat = freeSeat;
    }

    public Boolean getEnableDarkTheme() {
        return enableDarkTheme;
    }

    public void setEnableDarkTheme(Boolean enableDarkTheme) {
        this.enableDarkTheme = enableDarkTheme;
    }

    public Long getLongPin() {
        return longPin;
    }

    public void setLongPin(Long longPin) {
        this.longPin = longPin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getRememberPin() {
        return rememberPin;
    }

    public void setRememberPin(Boolean rememberPin) {
        this.rememberPin = rememberPin;
    }

    public Boolean getPartnerFromOld() {
        return partnerFromOld;
    }

    public void setPartnerFromOld(Boolean partnerFromOld) {
        this.partnerFromOld = partnerFromOld;
    }

    public Boolean getRenewPartner() {
        return renewPartner;
    }

    public void setRenewPartner(Boolean renewPartner) {
        this.renewPartner = renewPartner;
    }

    public String getStaticsUrl() {
        return staticsUrl;
    }

    public void setStaticsUrl(String staticsUrl) {
        this.staticsUrl = staticsUrl;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getReceiptLogo() {
        return receiptLogo;
    }

    public void setReceiptLogo(String receiptLogo) {
        this.receiptLogo = receiptLogo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Boolean getThirdPartyAgreement() {
        return thirdPartyAgreement;
    }

    public void setThirdPartyAgreement(Boolean thirdPartyAgreement) {
        this.thirdPartyAgreement = thirdPartyAgreement;
    }

    public Boolean getCommercialMailingAgreement() {
        return commercialMailingAgreement;
    }

    public void setCommercialMailingAgreement(Boolean commercialMailingAgreement) {
        this.commercialMailingAgreement = commercialMailingAgreement;
    }

    public Map<MemberPeriodType, MemberOperationPeriod> getMemberOperationPeriods() {
        return memberOperationPeriods;
    }

    public void setMemberOperationPeriods(Map<MemberPeriodType, MemberOperationPeriod> memberOperationPeriods) {
        this.memberOperationPeriods = memberOperationPeriods;
    }

    public List<MemberCapacity> getCapacities() {
        return capacities;
    }

    public void setCapacities(List<MemberCapacity> capacities) {
        this.capacities = capacities;
    }

    public List<SubscriptionMode> getSubscriptionModes() {
        return subscriptionModes;
    }

    public void setSubscriptionModes(List<SubscriptionMode> subscriptionModes) {
        this.subscriptionModes = subscriptionModes;
    }

    public List<Periodicity> getPeriodicityTranslations() {
        return periodicityTranslations;
    }

    public void setPeriodicityTranslations(List<Periodicity> periodicityTranslations) {
        this.periodicityTranslations = periodicityTranslations;
    }

    public List<RoleTranslation> getRoleTranslations() {
        return roleTranslations;
    }

    public void setRoleTranslations(List<RoleTranslation> roleTranslations) {
        this.roleTranslations = roleTranslations;
    }

    public AdditionalMemberMethod getAdditionalMemberMethod() {
        return additionalMemberMethod;
    }

    public void setAdditionalMemberMethod(AdditionalMemberMethod additionalMemberMethod) {
        this.additionalMemberMethod = additionalMemberMethod;
    }

    public Boolean getUserArea() {
        return userArea;
    }

    public void setUserArea(Boolean userArea) {
        this.userArea = userArea;
    }

    public Boolean getNewMember() {
        return newMember;
    }

    public void setNewMember(Boolean newMember) {
        this.newMember = newMember;
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

    public SubscriptionModeInferData getSubscriptionModeInferData() {
        return subscriptionModeInferData;
    }

    public void setSubscriptionModeInferData(SubscriptionModeInferData subscriptionModeInferData) {
        this.subscriptionModeInferData = subscriptionModeInferData;
    }

    public NewPriceCalculatorData getNewPriceCalculatorData() {
        return newPriceCalculatorData;
    }

    public void setNewPriceCalculatorData(NewPriceCalculatorData newPriceCalculatorData) {
        this.newPriceCalculatorData = newPriceCalculatorData;
    }

    public PreviousPriceCalculatorData getPreviousPriceCalculatorData() {
        return previousPriceCalculatorData;
    }

    public void setPreviousPriceCalculatorData(PreviousPriceCalculatorData previousPriceCalculatorData) {
        this.previousPriceCalculatorData = previousPriceCalculatorData;
    }

    public ChangeSeatValidatorData getChangeSeatValidatorData() {
        return changeSeatValidatorData;
    }

    public void setChangeSeatValidatorData(ChangeSeatValidatorData changeSeatValidatorData) {
        this.changeSeatValidatorData = changeSeatValidatorData;
    }

    public RenewalValidatorData getRenewalValidatorData() {
        return renewalValidatorData;
    }

    public void setRenewalValidatorData(RenewalValidatorData renewalValidatorData) {
        this.renewalValidatorData = renewalValidatorData;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public BuySeatValidatorData getBuySeatValidatorData() {
        return buySeatValidatorData;
    }

    public void setBuySeatValidatorData(BuySeatValidatorData buySeatValidatorData) {
        this.buySeatValidatorData = buySeatValidatorData;
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

    public Boolean getShowPeriodicity() {
        return showPeriodicity;
    }

    public void setShowPeriodicity(Boolean showPeriodicity) {
        this.showPeriodicity = showPeriodicity;
    }

    public QueueItConfig getQueueItConfig() {
        return queueItConfig;
    }

    public void setQueueItConfig(QueueItConfig queueItConfig) {
        this.queueItConfig = queueItConfig;
    }

    public Boolean getMemberEnabled() {
        return memberEnabled;
    }

    public void setMemberEnabled(Boolean memberEnabled) {
        this.memberEnabled = memberEnabled;
    }

    public PartnerRolesRelationInferData getPartnerRolesRelationInferData() {
        return partnerRolesRelationInferData;
    }

    public void setPartnerRolesRelationInferData(PartnerRolesRelationInferData partnerRolesRelationInferData) {
        this.partnerRolesRelationInferData = partnerRolesRelationInferData;
    }

    public DiscountCalculatorData getDiscountCalculatorData() {
        return discountCalculatorData;
    }

    public void setDiscountCalculatorData(DiscountCalculatorData discountCalculatorData) {
        this.discountCalculatorData = discountCalculatorData;
    }

    public List<Long> getMembershipVirtualZoneIds() {
        return membershipVirtualZoneIds;
    }

    public void setMembershipVirtualZoneIds(List<Long> membershipVirtualZoneIds) {
        this.membershipVirtualZoneIds = membershipVirtualZoneIds;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
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

    public Boolean getTransferSeat() {
        return transferSeat;
    }

    public void setTransferSeat(Boolean transferSeat) {
        this.transferSeat = transferSeat;
    }

    public List<MemberRestriction> getMemberRestrictions() {
        return memberRestrictions;
    }

    public void setMemberRestrictions(List<MemberRestriction> memberRestrictions) {
        this.memberRestrictions = memberRestrictions;
    }

    public List<String> getDownloadPassbookPermissions() {
        return downloadPassbookPermissions;
    }

    public void setDownloadPassbookPermissions(List<String> downloadPassbookPermissions) {
        this.downloadPassbookPermissions = downloadPassbookPermissions;
    }

    public String getNewMemberPermission() {
        return newMemberPermission;
    }

    public void setNewMemberPermission(String newMemberPermission) {
        this.newMemberPermission = newMemberPermission;
    }

    public String getBuySeatPermission() {
        return buySeatPermission;
    }

    public void setBuySeatPermission(String buySeatPermission) {
        this.buySeatPermission = buySeatPermission;
    }

    public Long getMembershipTermId() {
        return membershipTermId;
    }

    public void setMembershipTermId(Long membershipTermId) {
        this.membershipTermId = membershipTermId;
    }

    public Long getMembershipPeriodicityId() {
        return membershipPeriodicityId;
    }

    public void setMembershipPeriodicityId(Long membershipPeriodicityId) {
        this.membershipPeriodicityId = membershipPeriodicityId;
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

    public MembersCardImageContent getMembersCardImageContent() {
        return membersCardImageContent;
    }

    public void setMembersCardImageContent(MembersCardImageContent membersCardImageContent) {
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
