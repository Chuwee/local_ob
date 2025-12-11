package es.onebox.mgmt.channels.purchaseconfig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelBuyerRegistration;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelCommercialInformationConsent;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelPurchaseConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ChannelPurchaseConfigSessionsDTO sessions;
    @JsonProperty("include_taxes_separately")
    private Boolean includeTaxesSeparately;
    @JsonProperty("buyer_registration")
    private ChannelBuyerRegistration channelBuyerRegistration;
    @JsonProperty("commercial_information_consent")
    private ChannelCommercialInformationConsent channelCommercialInformationConsent;
    private ChannelPurchaseConfigVenueDTO venue;
    @JsonProperty("header_texts")
    private ChannelPurchaseConfigHeaderTextsDTO headerTexts;
    @JsonProperty("redirection_policy")
    @Valid
    private ChannelPurchaseConfigRedirectionPolicyDTO redirectionPolicy;
    @JsonProperty("show_accept_all_option")
    private Boolean showAcceptAllOption;
    @JsonProperty("add_related_channel")
    private Boolean addRelatedChannel;
    @JsonProperty("related_channel")
    private Long relatedChannel;
    @Valid
    @JsonProperty("invoice")
    private ChannelPurchaseConfigInvoiceDTO channelPurchaseInvoice;
    @Valid
    @JsonProperty("loyalty_program")
    private LoyaltyProgramDTO loyaltyProgram;
    @Valid
    @JsonProperty("allow_price_type_tag_filter")
    private Boolean allowPriceTypeTagFilter;
    @JsonProperty("price_display")
    private ChannelPriceDisplayConfigDTO priceDisplayConfig;

    public ChannelPurchaseConfigSessionsDTO getSessions() {
        return sessions;
    }

    public void setSessions(ChannelPurchaseConfigSessionsDTO sessions) {
        this.sessions = sessions;
    }

    public Boolean getIncludeTaxesSeparately() {
        return includeTaxesSeparately;
    }

    public void setIncludeTaxesSeparately(Boolean includeTaxesSeparately) {
        this.includeTaxesSeparately = includeTaxesSeparately;
    }

    public ChannelBuyerRegistration getChannelBuyerRegistration() {
        return channelBuyerRegistration;
    }

    public void setChannelBuyerRegistration(ChannelBuyerRegistration channelBuyerRegistration) {
        this.channelBuyerRegistration = channelBuyerRegistration;
    }

    public ChannelCommercialInformationConsent getChannelCommercialInformationConsent() {
        return channelCommercialInformationConsent;
    }

    public void setChannelCommercialInformationConsent(ChannelCommercialInformationConsent channelCommercialInformationConsent) {
        this.channelCommercialInformationConsent = channelCommercialInformationConsent;
    }

    public ChannelPurchaseConfigVenueDTO getVenue() {
        return venue;
    }

    public void setVenue(ChannelPurchaseConfigVenueDTO venue) {
        this.venue = venue;
    }

    public ChannelPurchaseConfigHeaderTextsDTO getHeaderTexts() {
        return headerTexts;
    }

    public void setHeaderTexts(ChannelPurchaseConfigHeaderTextsDTO headerTexts) {
        this.headerTexts = headerTexts;
    }

    public ChannelPurchaseConfigRedirectionPolicyDTO getRedirectionPolicy() {
        return redirectionPolicy;
    }

    public void setRedirectionPolicy(ChannelPurchaseConfigRedirectionPolicyDTO redirectionPolicy) {
        this.redirectionPolicy = redirectionPolicy;
    }

    public Boolean getShowAcceptAllOption() {
        return showAcceptAllOption;
    }

    public void setShowAcceptAllOption(Boolean showAcceptAllOption) {
        this.showAcceptAllOption = showAcceptAllOption;
    }

    public Long getRelatedChannel() {
        return relatedChannel;
    }

    public void setRelatedChannel(Long relatedChannel) {
        this.relatedChannel = relatedChannel;
    }

    public Boolean getAddRelatedChannel() {
        return addRelatedChannel;
    }

    public void setAddRelatedChannel(Boolean addRelatedChannel) {
        this.addRelatedChannel = addRelatedChannel;
    }

    public ChannelPurchaseConfigInvoiceDTO getChannelPurchaseInvoice() {
        return channelPurchaseInvoice;
    }

    public void setChannelPurchaseInvoice(ChannelPurchaseConfigInvoiceDTO channelPurchaseInvoice) {
        this.channelPurchaseInvoice = channelPurchaseInvoice;
    }

    public LoyaltyProgramDTO getLoyaltyProgram() { return loyaltyProgram; }

    public void setLoyaltyProgram(LoyaltyProgramDTO loyaltyProgram) { this.loyaltyProgram = loyaltyProgram; }

    public Boolean getAllowPriceTypeTagFilter() {
        return allowPriceTypeTagFilter;
    }

    public void setAllowPriceTypeTagFilter(Boolean allowPriceTypeTagFilter) {
        this.allowPriceTypeTagFilter = allowPriceTypeTagFilter;
    }

    public ChannelPriceDisplayConfigDTO getPriceDisplayConfig() {
        return priceDisplayConfig;
    }

    public void setPriceDisplayConfig(ChannelPriceDisplayConfigDTO priceDisplayConfig) {
        this.priceDisplayConfig = priceDisplayConfig;
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
