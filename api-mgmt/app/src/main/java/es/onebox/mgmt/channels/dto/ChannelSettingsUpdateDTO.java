package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelSettingsUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4397701435484850597L;

    @JsonProperty("automatic_seat_selection")
    private Boolean automaticSeatSelection;
    @JsonProperty("automatic_seat_selection_by_price_zone")
    private Boolean automaticSeatSelectionByPriceZone;
    @JsonProperty("enable_b2b")
    private Boolean enableB2B;
    @JsonProperty("allow_B2B_publishing")
    private Boolean allowB2BPublishing;
    @JsonProperty("enable_B2B_event_category_filter")
    private Boolean enableB2BEventCategoryFilter;
    @JsonProperty("surcharges")
    private SurchargesSettingsDTO surchargesSettings;
    @JsonProperty("use_multi_event")
    private Boolean useMultiEvent;
    @JsonProperty("allow_data_protection_fields")
    private Boolean allowDataProtectionFields;
    @JsonProperty("allow_linked_customers")
    private Boolean allowLinkedCustomers;
    @JsonProperty("use_robot_indexation")
    private Boolean useRobotIndexation;
    @JsonProperty("robots_no_follow")
    private Boolean robotsNoFollow;
    @JsonProperty("v4_enabled")
    private Boolean v4Enabled;
    @JsonProperty("v4_config_enabled")
    private Boolean v4ConfigEnabled;
    @JsonProperty("allow_download_passbook")
    private Boolean allowDownloadPassbook;
    @JsonProperty("invitations")
    private InvitationsSettingsDTO invitationsSettings;
    @JsonProperty("support_email")
    private SupportEmailDTO supportEmailDTO;
    @JsonProperty("donations")
    private DonationsConfigDTO donationsConfig;
    @JsonProperty("whatsapp")
    private WhatsappConfigDTO whatsappConfig;
    @JsonProperty("active_promotion")
    private Boolean activePromotion;
    @JsonProperty("use_currency_exchange")
    private Boolean useCurrencyExchange;
    @JsonProperty("currency_default_exchange")
    private String currencyDefaultExchange;
    @JsonProperty("customer_assignation")
    private CustomerAssignationDTO customerAssignation;
    @JsonProperty("enable_packs_and_events_catalog")
    private Boolean enablePacksAndEventsCatalog;
    @JsonProperty("destination_channel")
    private DestinationChannelDTO destinationChannel;

    public Boolean getAutomaticSeatSelection() {
        return automaticSeatSelection;
    }

    public void setAutomaticSeatSelection(Boolean automaticSeatSelection) {
        this.automaticSeatSelection = automaticSeatSelection;
    }

    public Boolean getAutomaticSeatSelectionByPriceZone() {
        return automaticSeatSelectionByPriceZone;
    }

    public void setAutomaticSeatSelectionByPriceZone(Boolean automaticSeatSelectionByPriceZone) {
        this.automaticSeatSelectionByPriceZone = automaticSeatSelectionByPriceZone;
    }

    public Boolean getEnableB2B() {
        return enableB2B;
    }

    public void setEnableB2B(Boolean enableB2B) {
        this.enableB2B = enableB2B;
    }

    public Boolean getAllowB2BPublishing() {
        return allowB2BPublishing;
    }

    public void setAllowB2BPublishing(Boolean allowB2BPublishing) {
        this.allowB2BPublishing = allowB2BPublishing;
    }

    public Boolean getEnableB2BEventCategoryFilter() {
        return enableB2BEventCategoryFilter;
    }

    public void setEnableB2BEventCategoryFilter(Boolean enableB2BEventCategoryFilter) {
        this.enableB2BEventCategoryFilter = enableB2BEventCategoryFilter;
    }

    public SurchargesSettingsDTO getSurchargesSettings() {
        return surchargesSettings;
    }

    public void setSurchargesSettings(SurchargesSettingsDTO surchargesSettings) {
        this.surchargesSettings = surchargesSettings;
    }

    public Boolean getUseMultiEvent() {
        return useMultiEvent;
    }

    public void setUseMultiEvent(Boolean useMultiEvent) {
        this.useMultiEvent = useMultiEvent;
    }

    public Boolean getAllowDataProtectionFields() {
        return allowDataProtectionFields;
    }

    public void setAllowDataProtectionFields(Boolean allowDataProtectionFields) {
        this.allowDataProtectionFields = allowDataProtectionFields;
    }

    public Boolean getAllowLinkedCustomers() {
        return allowLinkedCustomers;
    }

    public void setAllowLinkedCustomers(Boolean allowLinkedCustomers) {
        this.allowLinkedCustomers = allowLinkedCustomers;
    }

    public Boolean getUseRobotIndexation() {
        return useRobotIndexation;
    }

    public void setUseRobotIndexation(Boolean useRobotIndexation) {
        this.useRobotIndexation = useRobotIndexation;
    }

    public Boolean getRobotsNoFollow() {
        return robotsNoFollow;
    }

    public void setRobotsNoFollow(Boolean robotsNoFollow) {
        this.robotsNoFollow = robotsNoFollow;
    }

    public Boolean getV4Enabled() {
        return v4Enabled;
    }

    public void setV4Enabled(Boolean v4Enabled) {
        this.v4Enabled = v4Enabled;
    }

    public Boolean getV4ConfigEnabled() {
        return v4ConfigEnabled;
    }

    public void setV4ConfigEnabled(Boolean v4ConfigEnabled) {
        this.v4ConfigEnabled = v4ConfigEnabled;
    }

    public Boolean getAllowDownloadPassbook() {
        return allowDownloadPassbook;
    }

    public void setAllowDownloadPassbook(Boolean allowDownloadPassbook) {
        this.allowDownloadPassbook = allowDownloadPassbook;
    }

    public InvitationsSettingsDTO getInvitationsSettings() {
        return invitationsSettings;
    }

    public void setInvitationsSettings(InvitationsSettingsDTO invitationsSettings) {
        this.invitationsSettings = invitationsSettings;
    }

    public SupportEmailDTO getSupportEmailDTO() {
        return supportEmailDTO;
    }

    public void setSupportEmailDTO(SupportEmailDTO supportEmailDTO) {
        this.supportEmailDTO = supportEmailDTO;
    }

    public DonationsConfigDTO getDonationsConfig() {
        return donationsConfig;
    }

    public void setDonationsConfig(DonationsConfigDTO donationsConfig) {
        this.donationsConfig = donationsConfig;
    }

    public WhatsappConfigDTO getWhatsappConfig() {
        return whatsappConfig;
    }

    public void setWhatsappConfig(WhatsappConfigDTO whatsappConfig) {
        this.whatsappConfig = whatsappConfig;
    }

    public Boolean getActivePromotion() {
        return activePromotion;
    }

    public void setActivePromotion(Boolean activePromotion) {
        this.activePromotion = activePromotion;
    }

    public Boolean getUseCurrencyExchange() {
        return useCurrencyExchange;
    }

    public void setUseCurrencyExchange(Boolean useCurrencyExchange) {
        this.useCurrencyExchange = useCurrencyExchange;
    }

    public String getCurrencyDefaultExchange() {
        return currencyDefaultExchange;
    }

    public void setCurrencyDefaultExchange(String currencyDefaultExchange) {
        this.currencyDefaultExchange = currencyDefaultExchange;
    }

    public CustomerAssignationDTO getCustomerAssignation() {
        return customerAssignation;
    }

    public void setCustomerAssignation(CustomerAssignationDTO customerAssignation) {
        this.customerAssignation = customerAssignation;
    }

    public Boolean getEnablePacksAndEventsCatalog() {
        return enablePacksAndEventsCatalog;
    }

    public void setEnablePacksAndEventsCatalog(Boolean enablePacksAndEventsCatalog) {
        this.enablePacksAndEventsCatalog = enablePacksAndEventsCatalog;
    }

    public DestinationChannelDTO getDestinationChannel() {
        return destinationChannel;
    }

    public void setDestinationChannel(DestinationChannelDTO destinationChannel) {
        this.destinationChannel = destinationChannel;
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
