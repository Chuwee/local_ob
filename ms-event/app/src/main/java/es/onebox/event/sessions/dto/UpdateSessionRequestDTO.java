package es.onebox.event.sessions.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.sessions.enums.AccessScheduleType;
import es.onebox.event.sessions.enums.SessionVirtualQueueVersion;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateSessionRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private SessionStatus status;

    private SessionDateDTO date;
    private List<RateDTO> rates;
    private IdNameDTO ticketTax;
    private List<IdNameDTO> ticketTaxes;
    private IdNameDTO chargesTax;
    private List<IdNameDTO> chargesTaxes;
    private AccessScheduleType accessScheduleType;
    private IdNameDTO space;
    private Integer capacity;
    private Integer saleType;
    private Boolean useVenueConfigCapacity;
    private Boolean useTemplateAccess;

    private Boolean enableChannels;
    private Boolean enableBookings;
    private Boolean enableSales;
    private Boolean enableCaptcha;
    private Boolean enableShowDateInChannels;
    private Boolean enableShowTimeInChannels;
    private Boolean enableShowUnconfirmedDateInChannels;
    private Boolean enableOrphanSeats;
    private Boolean enableSecondaryMarket;

    private Boolean enableSessionTicketLimit;
    private Integer sessionTicketLimit;

    private Boolean enableSubscriptionList;
    private Integer subscriptionListId;

    private String unpublishReason;

    private DeleteSessionDTO deleteData;

    private Boolean enableMembersLoginsLimit;
    @Min(value = 1, message = "max members logins limit must be greater than 0")
    private Integer membersLoginsLimit;

    private SessionStreamingDTO streaming;

    private Boolean enableCountryFilter;
    private List<String> countries;

    private Boolean enableProducerTaxData;
    private Integer producerId;
    private Integer invoicePrefixId;

    private Boolean enableQueue;
    private String queueAlias;
    private SessionVirtualQueueVersion queueVersion;

    private String color;

    private String reference;
    private Boolean external;

    private Boolean presaleEnabled;
    private Integer presalePromotionId;

    private Boolean unconfirmedDate;

    private String externalReference;

    private PresalesRedirectionPolicyDTO presalesRedirectionPolicy;

    private Boolean highDemand;

    private SessionExternalConfigDTO sessionExternalConfig;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public SessionDateDTO getDate() {
        return date;
    }

    public void setDate(SessionDateDTO date) {
        this.date = date;
    }

    public List<RateDTO> getRates() {
        return rates;
    }

    public void setRates(List<RateDTO> rates) {
        this.rates = rates;
    }

    public IdNameDTO getTicketTax() {
        return ticketTax;
    }

    public void setTicketTax(IdNameDTO ticketTax) {
        this.ticketTax = ticketTax;
    }

    public IdNameDTO getChargesTax() {
        return chargesTax;
    }

    public void setChargesTax(IdNameDTO chargesTax) {
        this.chargesTax = chargesTax;
    }

    public List<IdNameDTO> getTicketTaxes() { return ticketTaxes; }

    public void setTicketTaxes(List<IdNameDTO> ticketTaxes) { this.ticketTaxes = ticketTaxes; }

    public List<IdNameDTO> getChargesTaxes() { return chargesTaxes; }

    public void setChargesTaxes(List<IdNameDTO> chargesTaxes) { this.chargesTaxes = chargesTaxes; }

    public AccessScheduleType getAccessScheduleType() {
        return accessScheduleType;
    }

    public void setAccessScheduleType(AccessScheduleType accessScheduleType) {
        this.accessScheduleType = accessScheduleType;
    }

    public IdNameDTO getSpace() {
        return space;
    }

    public void setSpace(IdNameDTO space) {
        this.space = space;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getEnableChannels() {
        return enableChannels;
    }

    public void setEnableChannels(Boolean enableChannels) {
        this.enableChannels = enableChannels;
    }

    public Boolean getEnableBookings() {
        return enableBookings;
    }

    public void setEnableBookings(Boolean enableBookings) {
        this.enableBookings = enableBookings;
    }

    public Boolean getEnableSales() {
        return enableSales;
    }

    public void setEnableSales(Boolean enableSales) {
        this.enableSales = enableSales;
    }

    public Boolean getEnableCaptcha() {
        return enableCaptcha;
    }

    public void setEnableCaptcha(Boolean enableCaptcha) {
        this.enableCaptcha = enableCaptcha;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public Boolean getUseVenueConfigCapacity() {
        return useVenueConfigCapacity;
    }

    public void setUseVenueConfigCapacity(Boolean useVenueConfigCapacity) {
        this.useVenueConfigCapacity = useVenueConfigCapacity;
    }

    public DeleteSessionDTO getDeleteData() {
        return deleteData;
    }

    public void setDeleteData(DeleteSessionDTO deleteData) {
        this.deleteData = deleteData;
    }

    public Boolean getEnableMembersLoginsLimit() {
        return enableMembersLoginsLimit;
    }

    public void setEnableMembersLoginsLimit(Boolean enableMembersLoginsLimit) {
        this.enableMembersLoginsLimit = enableMembersLoginsLimit;
    }

    public Integer getMembersLoginsLimit() {
        return membersLoginsLimit;
    }

    public void setMembersLoginsLimit(Integer membersLoginsLimit) {
        this.membersLoginsLimit = membersLoginsLimit;
    }

    public SessionStreamingDTO getStreaming() {
        return streaming;
    }

    public void setStreaming(SessionStreamingDTO streaming) {
        this.streaming = streaming;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getUseTemplateAccess() {
        return useTemplateAccess;
    }

    public void setUseTemplateAccess(Boolean useTemplateAccess) {
        this.useTemplateAccess = useTemplateAccess;
    }


    public Boolean getEnableShowDateInChannels() {
        return enableShowDateInChannels;
    }

    public void setEnableShowDateInChannels(Boolean enableShowDateInChannels) {
        this.enableShowDateInChannels = enableShowDateInChannels;
    }

    public Boolean getEnableShowTimeInChannels() {
        return enableShowTimeInChannels;
    }

    public void setEnableShowTimeInChannels(Boolean enableShowTimeInChannels) {
        this.enableShowTimeInChannels = enableShowTimeInChannels;
    }

    public Boolean getEnableShowUnconfirmedDateInChannels() {
        return enableShowUnconfirmedDateInChannels;
    }

    public void setEnableShowUnconfirmedDateInChannels(Boolean enableShowUnconfirmedDateInChannels) {
        this.enableShowUnconfirmedDateInChannels = enableShowUnconfirmedDateInChannels;
    }

    public Boolean getEnableOrphanSeats() {
        return enableOrphanSeats;
    }

    public void setEnableOrphanSeats(Boolean enableOrphanSeats) {
        this.enableOrphanSeats = enableOrphanSeats;
    }

    public Boolean getEnableSecondaryMarket() {
        return enableSecondaryMarket;
    }

    public void setEnableSecondaryMarket(Boolean enableSecondaryMarket) {
        this.enableSecondaryMarket = enableSecondaryMarket;
    }

    public Boolean getEnableSessionTicketLimit() {
        return enableSessionTicketLimit;
    }

    public void setEnableSessionTicketLimit(Boolean enableSessionTicketLimit) {
        this.enableSessionTicketLimit = enableSessionTicketLimit;
    }

    public Integer getSessionTicketLimit() {
        return sessionTicketLimit;
    }

    public void setSessionTicketLimit(Integer sessionTicketLimit) {
        this.sessionTicketLimit = sessionTicketLimit;
    }

    public Boolean getEnableProducerTaxData() {
        return enableProducerTaxData;
    }

    public void setEnableProducerTaxData(Boolean enableProducerTaxData) {
        this.enableProducerTaxData = enableProducerTaxData;
    }

    public Integer getProducerId() {
        return producerId;
    }

    public void setProducerId(Integer producerId) {
        this.producerId = producerId;
    }

    public Integer getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Integer invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
    }

    public Boolean getEnableSubscriptionList() {
        return enableSubscriptionList;
    }

    public void setEnableSubscriptionList(Boolean enableSubscriptionList) {
        this.enableSubscriptionList = enableSubscriptionList;
    }

    public Integer getSubscriptionListId() {
        return subscriptionListId;
    }

    public void setSubscriptionListId(Integer subscriptionListId) {
        this.subscriptionListId = subscriptionListId;
    }

    public Boolean getEnableCountryFilter() {
        return enableCountryFilter;
    }

    public void setEnableCountryFilter(Boolean enableCountryFilter) {
        this.enableCountryFilter = enableCountryFilter;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public Boolean getEnableQueue() {
        return enableQueue;
    }

    public void setEnableQueue(Boolean enableQueue) {
        this.enableQueue = enableQueue;
    }

    public String getQueueAlias() {
        return queueAlias;
    }

    public void setQueueAlias(String queueAlias) {
        this.queueAlias = queueAlias;
    }

    public SessionVirtualQueueVersion getQueueVersion() {
        return queueVersion;
    }

    public void setQueueVersion(SessionVirtualQueueVersion queueVersion) {
        this.queueVersion = queueVersion;
    }

    public String getUnpublishReason() {
        return unpublishReason;
    }

    public void setUnpublishReason(String unpublishReason) {
        this.unpublishReason = unpublishReason;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    public Boolean getPresaleEnabled() {
        return presaleEnabled;
    }

    public void setPresaleEnabled(Boolean presaleEnabled) {
        this.presaleEnabled = presaleEnabled;
    }

    public Integer getPresalePromotionId() {
        return presalePromotionId;
    }

    public void setPresalePromotionId(Integer presalePromotionId) {
        this.presalePromotionId = presalePromotionId;
    }

    public Boolean getUnconfirmedDate() {
        return unconfirmedDate;
    }

    public void setUnconfirmedDate(Boolean unconfirmedDate) {
        this.unconfirmedDate = unconfirmedDate;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public PresalesRedirectionPolicyDTO getPresalesRedirectionPolicy() {
        return presalesRedirectionPolicy;
    }

    public void setPresalesRedirectionPolicy(PresalesRedirectionPolicyDTO presalesRedirectionPolicy) {this.presalesRedirectionPolicy = presalesRedirectionPolicy;}

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

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
