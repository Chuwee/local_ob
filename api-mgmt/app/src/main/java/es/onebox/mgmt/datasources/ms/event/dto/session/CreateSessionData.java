package es.onebox.mgmt.datasources.ms.event.dto.session;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class CreateSessionData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Long venueConfigId;
    private Long taxId;
    private List<Long> ticketTaxIds;
    private Long chargeTaxId;
    private List<Long> chargeTaxIds;
    private List<Rate> rates;
    private ZonedDateTime sessionStartDate;
    private ZonedDateTime sessionEndDate;
    private ZonedDateTime publishDate;
    private ZonedDateTime salesStartDate;
    private ZonedDateTime salesEndDate;
    private ZonedDateTime bookingStartDate;
    private ZonedDateTime bookingEndDate;
    private ZonedDateTime secondaryMarketStartDate;
    private ZonedDateTime secondaryMarketEndDate;
    private Integer saleType;
    private Boolean seasonPass;
    private Boolean seasonTicket;
    private List<Long> seasonSessions;
    private String color;
    private Map<Long, Integer> seasonPassBlockingActions;
    private Long externalId;
    private Boolean allowPartialRefund;
    private String reference;
    private Boolean smartBooking;
    private Long entityId;
    private String externalSessionId;
    private LoyaltyPointsConfig loyaltyPointsConfig;
    private CreateSessionSettings settings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public Long getTaxId() {
        return taxId;
    }

    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    public List<Long> getTicketTaxIds() { return ticketTaxIds; }

    public void setTicketTaxIds(List<Long> ticketTaxIds) { this.ticketTaxIds = ticketTaxIds; }

    public List<Long> getChargeTaxIds() { return chargeTaxIds; }

    public void setChargeTaxIds(List<Long> chargeTaxIds) { this.chargeTaxIds = chargeTaxIds; }

    public Long getChargeTaxId() {
        return chargeTaxId;
    }

    public void setChargeTaxId(Long chargeTaxId) {
        this.chargeTaxId = chargeTaxId;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }

    public ZonedDateTime getSessionStartDate() {
        return sessionStartDate;
    }

    public void setSessionStartDate(ZonedDateTime sessionStartDate) {
        this.sessionStartDate = sessionStartDate;
    }

    public ZonedDateTime getSessionEndDate() {
        return sessionEndDate;
    }

    public void setSessionEndDate(ZonedDateTime sessionEndDate) {
        this.sessionEndDate = sessionEndDate;
    }

    public ZonedDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(ZonedDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public ZonedDateTime getSalesStartDate() {
        return salesStartDate;
    }

    public void setSalesStartDate(ZonedDateTime salesStartDate) {
        this.salesStartDate = salesStartDate;
    }

    public ZonedDateTime getSalesEndDate() {
        return salesEndDate;
    }

    public void setSalesEndDate(ZonedDateTime salesEndDate) {
        this.salesEndDate = salesEndDate;
    }

    public ZonedDateTime getBookingStartDate() {
        return bookingStartDate;
    }

    public void setBookingStartDate(ZonedDateTime bookingStartDate) {
        this.bookingStartDate = bookingStartDate;
    }

    public ZonedDateTime getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(ZonedDateTime bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public ZonedDateTime getSecondaryMarketStartDate() {
        return secondaryMarketStartDate;
    }

    public void setSecondaryMarketStartDate(ZonedDateTime secondaryMarketStartDate) {
        this.secondaryMarketStartDate = secondaryMarketStartDate;
    }

    public ZonedDateTime getSecondaryMarketEndDate() {
        return secondaryMarketEndDate;
    }

    public void setSecondaryMarketEndDate(ZonedDateTime secondaryMarketEndDate) {
        this.secondaryMarketEndDate = secondaryMarketEndDate;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public Boolean getSeasonPass() {
        return seasonPass;
    }

    public void setSeasonPass(Boolean seasonPass) {
        this.seasonPass = seasonPass;
    }

    public Boolean getSeasonTicket() {
        return seasonTicket;
    }

    public void setSeasonTicket(Boolean seasonTicket) {
        this.seasonTicket = seasonTicket;
    }

    public List<Long> getSeasonSessions() {
        return seasonSessions;
    }

    public void setSeasonSessions(List<Long> seasonSessions) {
        this.seasonSessions = seasonSessions;
    }

    public Map<Long, Integer> getSeasonPassBlockingActions() {
        return seasonPassBlockingActions;
    }

    public void setSeasonPassBlockingActions(Map<Long, Integer> seasonPassBlockingActions) {
        this.seasonPassBlockingActions = seasonPassBlockingActions;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getAllowPartialRefund() {
        return allowPartialRefund;
    }

    public void setAllowPartialRefund(Boolean allowPartialRefund) {
        this.allowPartialRefund = allowPartialRefund;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Boolean getSmartBooking() {
        return smartBooking;
    }

    public void setSmartBooking(Boolean smartBooking) {
        this.smartBooking = smartBooking;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getExternalSessionId() {
        return externalSessionId;
    }

    public void setExternalSessionId(String externalSessionId) {
        this.externalSessionId = externalSessionId;
    }

    public LoyaltyPointsConfig getLoyaltyPointsConfig() { return loyaltyPointsConfig; }

    public void setLoyaltyPointsConfig(LoyaltyPointsConfig loyaltyPointsConfig) {
        this.loyaltyPointsConfig = loyaltyPointsConfig;
    }

    public CreateSessionSettings getSettings() {
        return settings;
    }

    public void setSettings(CreateSessionSettings settings) {
        this.settings = settings;
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
