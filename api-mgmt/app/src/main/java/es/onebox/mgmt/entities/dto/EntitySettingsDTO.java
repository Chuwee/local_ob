package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.customdomains.common.dto.DomainSettings;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityQueueProvider;
import es.onebox.mgmt.entities.enums.EntityType;
import es.onebox.mgmt.entities.enums.MemberIdGeneration;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Set;

public class EntitySettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8119545965387260970L;

    private List<EntityType> types;

    private LanguagesDTO languages;

    @JsonProperty("external_integration")
    private ExternalIntegration externalIntegration;

    private Categories categories;

    @JsonProperty("corporate_color")
    private String corporateColor;

    @JsonProperty("enable_multievent_cart")
    private Boolean enableMultieventCart;

    @JsonProperty("allow_multi_avet_cart")
    private Boolean allowMultiAvetCart;

    @JsonProperty("allow_activity_events")
    private Boolean allowActivityEvents;

    @JsonProperty("allow_avet_integration")
    private Boolean allowAvetIntegration;

    @JsonProperty("live_streaming")
    private SettingsLiveStreamingDTO liveStreaming;

    @JsonProperty("allow_secondary_market")
    private Boolean allowSecondaryMarket;

    @JsonProperty("allow_png_conversion")
    private Boolean allowPngConversion;

    @JsonProperty("allow_attributes")
    private Boolean allowAttributes;

    @JsonProperty("enable_B2B")
    private Boolean enableB2B;

    @JsonProperty("allow_B2B_publishing")
    private Boolean allowB2BPublishing;

    @JsonProperty("allow_invitations")
    private Boolean allowInvitations;

    @Valid
    @JsonProperty("interactive_venue")
    private SettingsInteractiveVenueDTO interactiveVenue;

    @JsonProperty("allow_vip_views")
    private Boolean allowVipViews;

    @JsonProperty("allow_data_protection_fields")
    private Boolean allowDataProtectionFields;

    @JsonProperty("allow_members")
    private Boolean allowMembers;

    @JsonProperty("allow_digital_season_ticket")
    private Boolean allowDigitalSeasonTicket;

    @JsonProperty("allow_ticket_hide_price")
    private Boolean allowTicketHidePrice;

    @JsonProperty("allow_hard_ticket_pdf")
    private Boolean allowHardTicketPDF;

    @JsonProperty("enable_v4_configs")
    private Boolean enableV4Configs;

    @JsonProperty("allow_config_multiple_templates")
    private Boolean allowConfigMultipleTemplates;

    @Valid
    private SettingsNotificationsDTO notifications;

    private SettingsCustomizationDTO customization;

    @Valid
    @JsonProperty("bi_users")
    private EntitySettingsBIUsers biUsers;

    @JsonProperty("managed_entities")
    private List<IdNameDTO> managedEntities;

    @Valid
    @JsonProperty("accommodations")
    private EntityAccommodationsConfigDTO accommodationsConfig;

    @Valid
    @JsonProperty("whatsapp")
    private WhatsappConfigDTO whatsappConfig;

    @Valid
    @JsonProperty("donations")
    private Set<DonationsConfigDTO> donationsConfigDTO;

    @JsonProperty("allow_loyalty_points")
    private Boolean allowLoyaltyPoints;

    @JsonProperty("allow_friends")
    private Boolean allowFriends;

    @JsonProperty("session_duration")
    private Duration sessionDuration;

    @JsonProperty("account")
    private AccountSettingsDTO accountSettings;

    @JsonProperty("allow_fever_zone")
    private Boolean allowFeverZone;

    @JsonProperty("post_booking_questions")
    private PostBookingQuestionsDTO postBookingQuestions;

    @JsonProperty("queue_provider")
    private EntityQueueProvider queueProvider;

    @JsonProperty("allow_gateway_benefits")
    private Boolean allowGatewayBenefits;

    @JsonProperty("customers_domain_settings")
    private DomainSettings customersDomainSettings;

    @JsonProperty("member_id_generation")
    private MemberIdGeneration memberIdGeneration;

    @JsonProperty("allow_destination_channels")
    private Boolean allowDestinationChannels;

    private EntityCustomersDTO customers;

    public List<EntityType> getTypes() {
        return types;
    }

    public void setTypes(List<EntityType> types) {
        this.types = types;
    }

    public LanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(LanguagesDTO languages) {
        this.languages = languages;
    }

    public String getCorporateColor() {
        return corporateColor;
    }

    public void setCorporateColor(String corporateColor) {
        this.corporateColor = corporateColor;
    }

    public Boolean getEnableMultieventCart() {
        return enableMultieventCart;
    }

    public void setEnableMultieventCart(Boolean enableMultieventCart) {
        this.enableMultieventCart = enableMultieventCart;
    }

    public Boolean getAllowMultiAvetCart() {
        return allowMultiAvetCart;
    }

    public void setAllowMultiAvetCart(Boolean allowMultiAvetCart) {
        this.allowMultiAvetCart = allowMultiAvetCart;
    }

    public Boolean getAllowActivityEvents() {
        return allowActivityEvents;
    }

    public void setAllowActivityEvents(Boolean allowActivityEvents) {
        this.allowActivityEvents = allowActivityEvents;
    }

    public Boolean getAllowAvetIntegration() {
        return allowAvetIntegration;
    }

    public void setAllowAvetIntegration(Boolean allowAvetIntegration) {
        this.allowAvetIntegration = allowAvetIntegration;
    }

    public ExternalIntegration getExternalIntegration() {
        return externalIntegration;
    }

    public void setExternalIntegration(ExternalIntegration externalIntegration) {
        this.externalIntegration = externalIntegration;
    }

    public Categories getCategories() {
        return categories;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
    }

    public SettingsLiveStreamingDTO getLiveStreaming() {
        return liveStreaming;
    }

    public void setLiveStreaming(SettingsLiveStreamingDTO liveStreaming) {
        this.liveStreaming = liveStreaming;
    }

    public Boolean getAllowSecondaryMarket() {
        return allowSecondaryMarket;
    }

    public void setAllowSecondaryMarket(Boolean allowSecondaryMarket) {
        this.allowSecondaryMarket = allowSecondaryMarket;
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

    public Boolean getAllowInvitations() {
        return allowInvitations;
    }

    public void setAllowInvitations(Boolean allowInvitations) {
        this.allowInvitations = allowInvitations;
    }

    public SettingsInteractiveVenueDTO getInteractiveVenue() {
        return interactiveVenue;
    }

    public void setInteractiveVenue(SettingsInteractiveVenueDTO interactiveVenue) {
        this.interactiveVenue = interactiveVenue;
    }

    public Boolean getAllowAttributes() {
        return allowAttributes;
    }

    public void setAllowAttributes(Boolean allowAttributes) {
        this.allowAttributes = allowAttributes;
    }

    public Boolean getAllowVipViews() {
        return allowVipViews;
    }

    public void setAllowVipViews(Boolean allowVipViews) {
        this.allowVipViews = allowVipViews;
    }

    public Boolean getAllowDataProtectionFields() {
        return allowDataProtectionFields;
    }

    public void setAllowDataProtectionFields(Boolean allowDataProtectionFields) {
        this.allowDataProtectionFields = allowDataProtectionFields;
    }

    public Boolean getAllowMembers() {
        return allowMembers;
    }

    public void setAllowMembers(Boolean allowMembers) {
        this.allowMembers = allowMembers;
    }

    public Boolean getAllowDigitalSeasonTicket() {
        return allowDigitalSeasonTicket;
    }

    public void setAllowDigitalSeasonTicket(Boolean allowDigitalSeasonTicket) {
        this.allowDigitalSeasonTicket = allowDigitalSeasonTicket;
    }

    public Boolean getAllowTicketHidePrice() {
        return allowTicketHidePrice;
    }

    public void setAllowTicketHidePrice(Boolean allowTicketHidePrice) {
        this.allowTicketHidePrice = allowTicketHidePrice;
    }

    public Boolean getAllowHardTicketPDF() {
        return allowHardTicketPDF;
    }

    public void setAllowHardTicketPDF(Boolean allowHardTicketPDF) {
        this.allowHardTicketPDF = allowHardTicketPDF;
    }

    public SettingsNotificationsDTO getNotifications() {
        return notifications;
    }

    public void setNotifications(SettingsNotificationsDTO notifications) {
        this.notifications = notifications;
    }

    public SettingsCustomizationDTO getCustomization() {
        return customization;
    }

    public void setCustomization(SettingsCustomizationDTO customization) {
        this.customization = customization;
    }

    public EntitySettingsBIUsers getBiUsers() {
        return biUsers;
    }

    public void setBiUsers(EntitySettingsBIUsers biUsers) {
        this.biUsers = biUsers;
    }

    public List<IdNameDTO> getManagedEntities() {
        return managedEntities;
    }

    public void setManagedEntities(List<IdNameDTO> managedEntities) {
        this.managedEntities = managedEntities;
    }

    public EntityAccommodationsConfigDTO getAccommodationsConfig() {
        return accommodationsConfig;
    }

    public void setAccommodationsConfig(EntityAccommodationsConfigDTO accommodationsConfig) {
        this.accommodationsConfig = accommodationsConfig;
    }

    public WhatsappConfigDTO getWhatsappConfig() {
        return whatsappConfig;
    }

    public void setWhatsappConfig(WhatsappConfigDTO whatsappConfigDTO) {
        this.whatsappConfig = whatsappConfigDTO;
    }

    public Set<DonationsConfigDTO> getDonationsConfigDTO() {
        return donationsConfigDTO;
    }

    public void setDonationsConfigDTO(Set<DonationsConfigDTO> donationsConfigDTO) {
        this.donationsConfigDTO = donationsConfigDTO;
    }

    public Boolean getEnableV4Configs() {
        return enableV4Configs;
    }

    public void setEnableV4Configs(Boolean enableV4Configs) {
        this.enableV4Configs = enableV4Configs;
    }

    public Boolean getAllowLoyaltyPoints() {
        return allowLoyaltyPoints;
    }

    public void setAllowLoyaltyPoints(Boolean allowLoyaltyPoints) {
        this.allowLoyaltyPoints = allowLoyaltyPoints;
    }

    public Boolean getAllowFriends() {
        return allowFriends;
    }

    public void setAllowFriends(Boolean allowFriends) {
        this.allowFriends = allowFriends;
    }

    public Boolean getAllowPngConversion() { return allowPngConversion; }

    public void setAllowPngConversion(Boolean allowPngConversion) {
        this.allowPngConversion = allowPngConversion;
    }

    public Duration getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(Duration sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    public AccountSettingsDTO getAccountSettings() {
        return accountSettings;
    }

    public void setAccountSettings(AccountSettingsDTO accountSettings) {
        this.accountSettings = accountSettings;
    }

    public Boolean getAllowFeverZone() {
        return allowFeverZone;
    }

    public void setAllowFeverZone(Boolean allowFeverZone) {
        this.allowFeverZone = allowFeverZone;
    }

    public EntityQueueProvider getQueueProvider() {
        return queueProvider;
    }

    public void setQueueProvider(EntityQueueProvider queueProvider) {
        this.queueProvider = queueProvider;
    }

    public Boolean getAllowGatewayBenefits() {
        return allowGatewayBenefits;
    }

    public void setAllowGatewayBenefits(Boolean allowGatewayBenefits) {
        this.allowGatewayBenefits = allowGatewayBenefits;
    }

    public MemberIdGeneration getMemberIdGeneration() {
        return memberIdGeneration;
    }

    public void setMemberIdGeneration(MemberIdGeneration memberIdGeneration) {
        this.memberIdGeneration = memberIdGeneration;
    }

    public PostBookingQuestionsDTO getPostBookingQuestions() {
        return postBookingQuestions;
    }

    public void setPostBookingQuestions(PostBookingQuestionsDTO postBookingQuestions) {
        this.postBookingQuestions = postBookingQuestions;
    }

    public DomainSettings getCustomersDomainSettings() {
        return customersDomainSettings;
    }

    public void setCustomersDomainSettings(DomainSettings customersDomainSettings) {
        this.customersDomainSettings = customersDomainSettings;
    }

    public Boolean getAllowConfigMultipleTemplates() {
        return allowConfigMultipleTemplates;
    }

    public void setAllowConfigMultipleTemplates(Boolean allowConfigMultipleTemplates) {
        this.allowConfigMultipleTemplates = allowConfigMultipleTemplates;
    }

    public EntityCustomersDTO getCustomers() {
        return customers;
    }

    public void setCustomers(EntityCustomersDTO customers) {
        this.customers = customers;
    }

    public Boolean getAllowDestinationChannels() {
        return allowDestinationChannels;
    }

    public void setAllowDestinationChannels(Boolean allowDestinationChannels) {
        this.allowDestinationChannels = allowDestinationChannels;
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
