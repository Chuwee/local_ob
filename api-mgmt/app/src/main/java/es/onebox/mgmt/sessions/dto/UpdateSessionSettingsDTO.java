package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateSessionSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7318358541627814953L;

    @Valid
    private List<RateDTO> rates;

    @Valid
    private TaxesDTO taxes;

    private SetttingsReleaseDTO release;

    private SettingsBookingsDTO booking;

    private SettingsSalesDTO sale;

    @JsonProperty("access_control")
    private SettingsAccessControlDTO accessControl;

    @JsonProperty("live_streaming")
    private SettingsLiveStreamingDTO liveStreaming;

    @JsonProperty("enable_captcha")
    private Boolean enableCaptcha;

    @JsonProperty("enable_orphan_seats")
    private Boolean enableOrphanSeats;

    @JsonProperty("activity_sale_type")
    private SessionSaleType activitySaleType;

    @JsonProperty("use_venue_template_capacity_config")
    private Boolean useVenueTemplateCapacityConfig;

    @JsonProperty("use_venue_template_access")
    private Boolean useVenueTemplateAccess;

    @JsonProperty("attendant_tickets")
    private SessionAttendantTicketsDTO attendantTickets;

    @JsonProperty("session_pack")
    private SessionPackSettingsDTO sessionPackSettings;

    @Valid
    @JsonProperty("country_filter")
    private SessionCountryFilterDTO sessionCountryFilter;

    @Valid
    @JsonProperty("limits")
    private SessionSettingsLimitsDTO limits;

    @JsonProperty("channels")
    private SessionChannelSettingsDTO sessionChannelSettings;

    @Valid
    @JsonProperty("virtual_queue")
    private SessionVirtualQueueDTO sessionVirtualQueue;

    @JsonProperty("subscription_list")
    private SessionSubscriptionListDTO subscriptionList;

    @JsonProperty("secondary_market_sale")
    private SettingsSecondaryMarketDTO secondaryMarket;


    @JsonProperty("presales_redirection_policy")
    private PresalesRedirectionPolicyDTO presalesRedirectionPolicy;

    @JsonProperty("high_demand")
    private Boolean highDemand;

    @JsonProperty("session_external_config")
    private SessionExternalConfigDTO sessionExternalConfig;

    @JsonProperty("use_dynamic_prices")
    private Boolean useDynamicPrices;

    @Valid
    public List<RateDTO> getRates() {
        return rates;
    }

    public void setRates(List<RateDTO> rates) {
        this.rates = rates;
    }

    public TaxesDTO getTaxes() {
        return taxes;
    }

    public void setTaxes(TaxesDTO taxes) {
        this.taxes = taxes;
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

    public SettingsAccessControlDTO getAccessControl() {
        return accessControl;
    }

    public void setAccessControl(SettingsAccessControlDTO accessControl) {
        this.accessControl = accessControl;
    }

    public SettingsLiveStreamingDTO getLiveStreaming() {
        return liveStreaming;
    }

    public void setLiveStreaming(SettingsLiveStreamingDTO liveStreaming) {
        this.liveStreaming = liveStreaming;
    }

    public Boolean getEnableCaptcha() {
        return enableCaptcha;
    }

    public void setEnableCaptcha(Boolean enableCaptcha) {
        this.enableCaptcha = enableCaptcha;
    }

    public Boolean getEnableOrphanSeats() {
        return enableOrphanSeats;
    }

    public void setEnableOrphanSeats(Boolean enableOrphanSeats) {
        this.enableOrphanSeats = enableOrphanSeats;
    }

    public SessionSaleType getActivitySaleType() {
        return activitySaleType;
    }

    public void setActivitySaleType(SessionSaleType activitySaleType) {
        this.activitySaleType = activitySaleType;
    }

    public Boolean getUseVenueTemplateCapacityConfig() {
        return useVenueTemplateCapacityConfig;
    }

    public void setUseVenueTemplateCapacityConfig(Boolean useVenueTemplateCapacityConfig) {
        this.useVenueTemplateCapacityConfig = useVenueTemplateCapacityConfig;
    }

    public Boolean getUseVenueTemplateAccess() {
        return useVenueTemplateAccess;
    }

    public void setUseVenueTemplateAccess(Boolean useVenueTemplateAccess) {
        this.useVenueTemplateAccess = useVenueTemplateAccess;
    }

    public SessionAttendantTicketsDTO getAttendantTickets() {
        return attendantTickets;
    }

    public void setAttendantTickets(SessionAttendantTicketsDTO attendantTickets) {
        this.attendantTickets = attendantTickets;
    }

    public SessionPackSettingsDTO getSessionPackSettings() {
        return sessionPackSettings;
    }

    public void setSessionPackSettings(SessionPackSettingsDTO sessionPackSettings) {
        this.sessionPackSettings = sessionPackSettings;
    }

    public SessionSettingsLimitsDTO getLimits() {
        return limits;
    }

    public void setLimits(SessionSettingsLimitsDTO limits) {
        this.limits = limits;
    }

    public SessionChannelSettingsDTO getSessionChannelSettings() {
        return sessionChannelSettings;
    }

    public void setSessionChannelSettings(SessionChannelSettingsDTO sessionChannelSettings) {
        this.sessionChannelSettings = sessionChannelSettings;
    }

    public SessionSubscriptionListDTO getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(SessionSubscriptionListDTO subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    public SessionCountryFilterDTO getSessionCountryFilter() {
        return sessionCountryFilter;
    }

    public void setSessionCountryFilter(SessionCountryFilterDTO sessionCountryFilter) {
        this.sessionCountryFilter = sessionCountryFilter;
    }

    public SessionVirtualQueueDTO getSessionVirtualQueue() {
        return sessionVirtualQueue;
    }

    public void setSessionVirtualQueue(SessionVirtualQueueDTO sessionVirtualQueue) {
        this.sessionVirtualQueue = sessionVirtualQueue;
    }

    public SettingsSecondaryMarketDTO getSecondaryMarket() {
        return secondaryMarket;
    }

    public void setSecondaryMarket(SettingsSecondaryMarketDTO secondaryMarket) {
        this.secondaryMarket = secondaryMarket;
    }

    public PresalesRedirectionPolicyDTO getPresalesRedirectionPolicy() {
        return presalesRedirectionPolicy;
    }

    public void setPresalesRedirectionPolicy(PresalesRedirectionPolicyDTO presalesRedirectionPolicy) {
        this.presalesRedirectionPolicy = presalesRedirectionPolicy;
    }

    public Boolean getHighDemand() {
        return highDemand;
    }

    public void setHighDemand(Boolean highDemand) {
        this.highDemand = highDemand;
    }

    public SessionExternalConfigDTO getSessionExternalConfig() {
        return sessionExternalConfig;
    }

    public void setSessionExternalConfig(SessionExternalConfigDTO sessionExternalConfig) {
        this.sessionExternalConfig = sessionExternalConfig;
    }

    public Boolean getUseDynamicPrices() {
        return useDynamicPrices;
    }

    public void setUseDynamicPrices(Boolean useDynamicPrices) {
        this.useDynamicPrices = useDynamicPrices;
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
