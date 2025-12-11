package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class CreateSessionDTO {

    private Long sessionId;
    private Long eventId;
    private Long venueConfigId;
    private Long venueEntityConfigId;
    private String name;
    private String description;
    private ZonedDateTime sessionStartDate;
    private ZonedDateTime sessionEndDate;
    private ZonedDateTime publishDate;
    private ZonedDateTime salesStartDate;
    private ZonedDateTime salesEndDate;
    private ZonedDateTime secondaryMarketStartDate;
    private ZonedDateTime secondaryMarketEndDate;
    private Integer duration;
    private List<RateDTO> rates;
    private SessionStatus status;
    private Integer capacityGenerationStatus;
    private Integer capacityRegenerationStatus;
    private Integer capacity;
    private Boolean published;
    private Boolean onSale;
    private Integer canceledSaleReason;
    private Integer canceledPublishReason;
    private Boolean migratedBIProducer;
    private Boolean migratedBIChannel;
    private String color;
    private ZonedDateTime bookingStartDate;
    private ZonedDateTime bookingEndDate;
    private Boolean bookings;
    private Integer purgeStatus;
    private Long externalId;
    private Long taxId;
    private List<Long> ticketTaxIds;
    private Long chargeTaxId;
    private List<Long> chargeTaxIds;
    private Boolean finalDate;
    private ZonedDateTime externalModificationDate;
    private Long accessValidationSpaceId;
    private Integer typeScheduleAccess;
    private ZonedDateTime gateOpenTime;
    private ZonedDateTime gateCloseTime;
    private Boolean dirtyBI;
    private Integer maxTicketsPerSale;
    private Boolean captcha;
    private Long boxOfficeCommElements;
    private Long ticketCommElements;
    private Boolean showSchedule;
    private Boolean venueConfigBind;
    private Integer saleType;
    private Boolean useTemplateAccess;
    private Boolean useLimitsQuotasTemplateEvent;
    private Boolean useProducerTaxData;
    private Long producerId;
    private Long invoicePrefixId;
    private String publishCancelReason;
    private Long mailingListId;
    private Boolean external;
    private Boolean preview;
    private Boolean hideSessionDates;
    private Boolean showDate;
    private Boolean showDatetime;
    private Boolean showUnconfirmedDate;
    private ZonedDateTime createDate;
    private ZonedDateTime updateDate;
    private Boolean seasonPass;
    private Boolean allowPartialRefund;
    private Boolean seasonTicket;
    private List<Long> seasonSessions;
    private Map<Integer, Integer> seasonPassBlockingActions;
    private String reference;
    private Boolean smartBooking;
    private CreateSessionLoyaltyPointsConfigDTO loyaltyPointsConfig;
    private CreateSessionSettingsDTO settings;

    public CreateSessionDTO() {
    }

    public CreateSessionDTO(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public Long getVenueEntityConfigId() {
        return venueEntityConfigId;
    }

    public void setVenueEntityConfigId(Long venueEntityConfigId) {
        this.venueEntityConfigId = venueEntityConfigId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public ZonedDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(ZonedDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<RateDTO> getRates() {
        return rates;
    }

    public void setRates(List<RateDTO> rates) {
        this.rates = rates;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public Integer getCapacityGenerationStatus() {
        return capacityGenerationStatus;
    }

    public void setCapacityGenerationStatus(Integer capacityGenerationStatus) {
        this.capacityGenerationStatus = capacityGenerationStatus;
    }

    public Integer getCapacityRegenerationStatus() {
        return capacityRegenerationStatus;
    }

    public void setCapacityRegenerationStatus(Integer capacityRegenerationStatus) {
        this.capacityRegenerationStatus = capacityRegenerationStatus;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public Integer getCanceledSaleReason() {
        return canceledSaleReason;
    }

    public void setCanceledSaleReason(Integer canceledSaleReason) {
        this.canceledSaleReason = canceledSaleReason;
    }

    public Integer getCanceledPublishReason() {
        return canceledPublishReason;
    }

    public void setCanceledPublishReason(Integer canceledPublishReason) {
        this.canceledPublishReason = canceledPublishReason;
    }

    public Boolean getMigratedBIProducer() {
        return migratedBIProducer;
    }

    public void setMigratedBIProducer(Boolean migratedBIProducer) {
        this.migratedBIProducer = migratedBIProducer;
    }

    public Boolean getMigratedBIChannel() {
        return migratedBIChannel;
    }

    public void setMigratedBIChannel(Boolean migratedBIChannel) {
        this.migratedBIChannel = migratedBIChannel;
    }

    public Boolean getSeasonPass() {
        return seasonPass;
    }

    public void setSeasonPass(Boolean seasonPass) {
        this.seasonPass = seasonPass;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public Boolean getBookings() {
        return bookings;
    }

    public void setBookings(Boolean bookings) {
        this.bookings = bookings;
    }

    public Integer getPurgeStatus() {
        return purgeStatus;
    }

    public void setPurgeStatus(Integer purgeStatus) {
        this.purgeStatus = purgeStatus;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
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

    public Boolean getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Boolean finalDate) {
        this.finalDate = finalDate;
    }

    public ZonedDateTime getExternalModificationDate() {
        return externalModificationDate;
    }

    public void setExternalModificationDate(ZonedDateTime externalModificationDate) {
        this.externalModificationDate = externalModificationDate;
    }

    public Long getAccessValidationSpaceId() {
        return accessValidationSpaceId;
    }

    public void setAccessValidationSpaceId(Long accessValidationSpaceId) {
        this.accessValidationSpaceId = accessValidationSpaceId;
    }

    public Integer getTypeScheduleAccess() {
        return typeScheduleAccess;
    }

    public void setTypeScheduleAccess(Integer typeScheduleAccess) {
        this.typeScheduleAccess = typeScheduleAccess;
    }

    public ZonedDateTime getGateOpenTime() {
        return gateOpenTime;
    }

    public void setGateOpenTime(ZonedDateTime gateOpenTime) {
        this.gateOpenTime = gateOpenTime;
    }

    public ZonedDateTime getGateCloseTime() {
        return gateCloseTime;
    }

    public void setGateCloseTime(ZonedDateTime gateCloseTime) {
        this.gateCloseTime = gateCloseTime;
    }

    public Boolean getDirtyBI() {
        return dirtyBI;
    }

    public void setDirtyBI(Boolean dirtyBI) {
        this.dirtyBI = dirtyBI;
    }

    public Integer getMaxTicketsPerSale() {
        return maxTicketsPerSale;
    }

    public void setMaxTicketsPerSale(Integer maxTicketsPerSale) {
        this.maxTicketsPerSale = maxTicketsPerSale;
    }

    public Boolean getCaptcha() {
        return captcha;
    }

    public void setCaptcha(Boolean captcha) {
        this.captcha = captcha;
    }

    public Long getBoxOfficeCommElements() {
        return boxOfficeCommElements;
    }

    public void setBoxOfficeCommElements(Long boxOfficeCommElements) {
        this.boxOfficeCommElements = boxOfficeCommElements;
    }

    public Long getTicketCommElements() {
        return ticketCommElements;
    }

    public void setTicketCommElements(Long ticketCommElements) {
        this.ticketCommElements = ticketCommElements;
    }

    public Boolean getShowSchedule() {
        return showSchedule;
    }

    public void setShowSchedule(Boolean showSchedule) {
        this.showSchedule = showSchedule;
    }

    public Boolean getVenueConfigBind() {
        return venueConfigBind;
    }

    public void setVenueConfigBind(Boolean venueConfigBind) {
        this.venueConfigBind = venueConfigBind;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public Boolean getUseTemplateAccess() {
        return useTemplateAccess;
    }

    public void setUseTemplateAccess(Boolean useTemplateAccess) {
        this.useTemplateAccess = useTemplateAccess;
    }

    public Boolean getUseLimitsQuotasTemplateEvent() {
        return useLimitsQuotasTemplateEvent;
    }

    public void setUseLimitsQuotasTemplateEvent(Boolean useLimitsQuotasTemplateEvent) {
        this.useLimitsQuotasTemplateEvent = useLimitsQuotasTemplateEvent;
    }

    public Boolean getUseProducerTaxData() {
        return useProducerTaxData;
    }

    public void setUseProducerTaxData(Boolean useProducerTaxData) {
        this.useProducerTaxData = useProducerTaxData;
    }

    public Long getProducerId() {
        return producerId;
    }

    public void setProducerId(Long producerId) {
        this.producerId = producerId;
    }

    public Long getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Long invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
    }

    public String getPublishCancelReason() {
        return publishCancelReason;
    }

    public void setPublishCancelReason(String publishCancelReason) {
        this.publishCancelReason = publishCancelReason;
    }

    public Long getMailingListId() {
        return mailingListId;
    }

    public void setMailingListId(Long mailingListId) {
        this.mailingListId = mailingListId;
    }

    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    public Boolean getPreview() {
        return preview;
    }

    public void setPreview(Boolean preview) {
        this.preview = preview;
    }

    public Boolean getHideSessionDates() {
        return hideSessionDates;
    }

    public void setHideSessionDates(Boolean hideSessionDates) {
        this.hideSessionDates = hideSessionDates;
    }

    public Boolean getShowDate() {
        return showDate;
    }

    public void setShowDate(Boolean showDate) {
        this.showDate = showDate;
    }

    public Boolean getShowDatetime() {
        return showDatetime;
    }

    public void setShowDatetime(Boolean showDatetime) {
        this.showDatetime = showDatetime;
    }

    public Boolean getShowUnconfirmedDate() {
        return showUnconfirmedDate;
    }

    public void setShowUnconfirmedDate(Boolean showUnconfirmedDate) {
        this.showUnconfirmedDate = showUnconfirmedDate;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    public ZonedDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(ZonedDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public List<Long> getSeasonSessions() {
        return seasonSessions;
    }

    public void setSeasonSessions(List<Long> seasonSessions) {
        this.seasonSessions = seasonSessions;
    }

    public Map<Integer, Integer> getSeasonPassBlockingActions() {
        return seasonPassBlockingActions;
    }

    public void setSeasonPassBlockingActions(Map<Integer, Integer> seasonPassBlockingActions) {
        this.seasonPassBlockingActions = seasonPassBlockingActions;
    }

    public Boolean getSeasonTicket() {
        return seasonTicket;
    }

    public void setSeasonTicket(Boolean seasonTicket) {
        this.seasonTicket = seasonTicket;
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

    public CreateSessionLoyaltyPointsConfigDTO getLoyaltyPointsConfig() { return loyaltyPointsConfig; }

    public void setLoyaltyPointsConfig(CreateSessionLoyaltyPointsConfigDTO loyaltyPointsConfig) {
        this.loyaltyPointsConfig = loyaltyPointsConfig;
    }

    public CreateSessionSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(CreateSessionSettingsDTO settings) {
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
