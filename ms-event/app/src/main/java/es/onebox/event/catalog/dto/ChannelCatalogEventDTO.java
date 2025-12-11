package es.onebox.event.catalog.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventPostBookingQuestions;
import es.onebox.event.events.dto.TaxonomyDTO;
import es.onebox.event.events.dto.conditions.ProfessionalClientConditions;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

public class ChannelCatalogEventDTO extends IdNameDTO {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private EventType type;
    private EventStatus eventStatus;
    private Boolean forSale;
    private Boolean soldOut;
    private Boolean isHighlighted;
    private ZonedDateTime publishDate;
    private String publishDateTimeZone;
    private ZonedDateTime startDate;
    private String startDateTimeZone;
    private ZonedDateTime endDate;
    private String endDateTimeZone;
    private ZonedDateTime startSaleDate;
    private String startSaleDateTimeZone;
    private ZonedDateTime endSaleDate;
    private String endSaleDateTimeZone;
    private ZonedDateTime startBookingDate;
    private ZonedDateTime endBookingDate;
    private Boolean supraEvent;
    private Boolean giftTicket;
    private Boolean multiVenue;
    private Boolean multiLocation;
    private List<CatalogCommunicationElementDTO> communicationElements;
    private String defaultLanguage;
    private List<String> languages;
    private List<CatalogVenueDTO> venues;
    private String externalReference;
    private String promoterRef;
    private TaxonomyDTO taxonomy;
    private TaxonomyDTO parentTaxonomy;
    private TaxonomyDTO customTaxonomy;
    private TaxonomyDTO customParentTaxonomy;
    private TourInfoDTO tour;
    private CatalogPrices prices;
    private Boolean useTieredPricing;
    private List<Integer> eventAttributesId;
    private List<Integer> eventAttributesValueId;
    private Long entityId;
    private Boolean sessionsShowDate;
    private Boolean sessionsShowDateTime;
    private Boolean sessionsShowSchedule;
    private Boolean sessionsNoFinalDate;
    private Boolean ticketHandling;
    private Boolean hasSessions;
    private Boolean hasSessionPacks;
    private Integer carouselPosition;
    private Boolean extended;
    private Integer currencyId;
    private SeasonPackSettingsDTO seasonPackSettingsDTO;
    private ProfessionalClientConditions professionalClientConditions;
    private Boolean phoneValidationRequired;
    private Boolean attendantVerificationRequired;
    private ChannelEventPostBookingQuestions postBookingQuestions;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public EventStatus getEventStatus() { return eventStatus; }

    public void setEventStatus(EventStatus eventStatus) { this.eventStatus = eventStatus; }

    public Boolean getForSale() {
        return forSale;
    }

    public void setForSale(Boolean forSale) {
        this.forSale = forSale;
    }

    public Boolean getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    public Boolean getHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(Boolean highlighted) {
        isHighlighted = highlighted;
    }

    public ZonedDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(ZonedDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishDateTimeZone() {
        return publishDateTimeZone;
    }

    public void setPublishDateTimeZone(String publishDateTimeZone) {
        this.publishDateTimeZone = publishDateTimeZone;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public String getStartDateTimeZone() {
        return startDateTimeZone;
    }

    public void setStartDateTimeZone(String startDateTimeZone) {
        this.startDateTimeZone = startDateTimeZone;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public String getEndDateTimeZone() {
        return endDateTimeZone;
    }

    public void setEndDateTimeZone(String endDateTimeZone) {
        this.endDateTimeZone = endDateTimeZone;
    }

    public ZonedDateTime getStartSaleDate() {
        return startSaleDate;
    }

    public void setStartSaleDate(ZonedDateTime startSaleDate) {
        this.startSaleDate = startSaleDate;
    }

    public String getStartSaleDateTimeZone() {
        return startSaleDateTimeZone;
    }

    public void setStartSaleDateTimeZone(String startSaleDateTimeZone) {
        this.startSaleDateTimeZone = startSaleDateTimeZone;
    }

    public ZonedDateTime getEndSaleDate() {
        return endSaleDate;
    }

    public void setEndSaleDate(ZonedDateTime endSaleDate) {
        this.endSaleDate = endSaleDate;
    }

    public String getEndSaleDateTimeZone() {
        return endSaleDateTimeZone;
    }

    public void setEndSaleDateTimeZone(String endSaleDateTimeZone) {
        this.endSaleDateTimeZone = endSaleDateTimeZone;
    }

    public ZonedDateTime getStartBookingDate() {
        return startBookingDate;
    }

    public void setStartBookingDate(ZonedDateTime startBookingDate) {
        this.startBookingDate = startBookingDate;
    }

    public ZonedDateTime getEndBookingDate() {
        return endBookingDate;
    }

    public void setEndBookingDate(ZonedDateTime endBookingDate) {
        this.endBookingDate = endBookingDate;
    }

    public Boolean getSupraEvent() {
        return supraEvent;
    }

    public void setSupraEvent(Boolean supraEvent) {
        this.supraEvent = supraEvent;
    }

    public Boolean getGiftTicket() {
        return giftTicket;
    }

    public void setGiftTicket(Boolean giftTicket) {
        this.giftTicket = giftTicket;
    }

    public Boolean getMultiVenue() {
        return multiVenue;
    }

    public void setMultiVenue(Boolean multiVenue) {
        this.multiVenue = multiVenue;
    }

    public Boolean getMultiLocation() {
        return multiLocation;
    }

    public void setMultiLocation(Boolean multiLocation) {
        this.multiLocation = multiLocation;
    }

    public List<CatalogCommunicationElementDTO> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<CatalogCommunicationElementDTO> communicationElements) {
        this.communicationElements = communicationElements;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<CatalogVenueDTO> getVenues() {
        return venues;
    }

    public void setVenues(List<CatalogVenueDTO> venues) {
        this.venues = venues;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getPromoterRef() {
        return promoterRef;
    }

    public void setPromoterRef(String promoterRef) {
        this.promoterRef = promoterRef;
    }

    public TaxonomyDTO getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(TaxonomyDTO taxonomy) {
        this.taxonomy = taxonomy;
    }

    public TaxonomyDTO getParentTaxonomy() {
        return parentTaxonomy;
    }

    public void setParentTaxonomy(TaxonomyDTO parentTaxonomy) {
        this.parentTaxonomy = parentTaxonomy;
    }

    public TaxonomyDTO getCustomTaxonomy() {
        return customTaxonomy;
    }

    public void setCustomTaxonomy(TaxonomyDTO customTaxonomy) {
        this.customTaxonomy = customTaxonomy;
    }

    public TaxonomyDTO getCustomParentTaxonomy() {
        return customParentTaxonomy;
    }

    public void setCustomParentTaxonomy(TaxonomyDTO customParentTaxonomy) {
        this.customParentTaxonomy = customParentTaxonomy;
    }

    public CatalogPrices getPrices() {
        return prices;
    }

    public void setPrices(CatalogPrices prices) {
        this.prices = prices;
    }

    public Boolean getUseTieredPricing() {
        return useTieredPricing;
    }

    public void setUseTieredPricing(Boolean useTieredPricing) {
        this.useTieredPricing = useTieredPricing;
    }

    public List<Integer> getEventAttributesId() {
        return eventAttributesId;
    }

    public void setEventAttributesId(List<Integer> eventAttributesId) {
        this.eventAttributesId = eventAttributesId;
    }

    public List<Integer> getEventAttributesValueId() {
        return eventAttributesValueId;
    }

    public void setEventAttributesValueId(List<Integer> eventAttributesValueId) {
        this.eventAttributesValueId = eventAttributesValueId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Boolean getSessionsShowDate() {
        return sessionsShowDate;
    }

    public void setSessionsShowDate(Boolean sessionsShowDate) {
        this.sessionsShowDate = sessionsShowDate;
    }

    public Boolean getSessionsShowDateTime() {
        return sessionsShowDateTime;
    }

    public void setSessionsShowDateTime(Boolean sessionsShowDateTime) {
        this.sessionsShowDateTime = sessionsShowDateTime;
    }

    public Boolean getSessionsShowSchedule() {
        return sessionsShowSchedule;
    }

    public void setSessionsShowSchedule(Boolean sessionsShowSchedule) {
        this.sessionsShowSchedule = sessionsShowSchedule;
    }

    public Boolean getSessionsNoFinalDate() {
        return sessionsNoFinalDate;
    }

    public void setSessionsNoFinalDate(Boolean sessionsNoFinalDate) {
        this.sessionsNoFinalDate = sessionsNoFinalDate;
    }

    public Boolean getTicketHandling() {
        return ticketHandling;
    }

    public void setTicketHandling(Boolean ticketHandling) {
        this.ticketHandling = ticketHandling;
    }

    public Boolean getHasSessions() {
        return hasSessions;
    }

    public void setHasSessions(Boolean hasSessions) {
        this.hasSessions = hasSessions;
    }

    public Boolean getHasSessionPacks() {
        return hasSessionPacks;
    }

    public void setHasSessionPacks(Boolean hasSessionPacks) {
        this.hasSessionPacks = hasSessionPacks;
    }

    public Integer getCarouselPosition() {
        return carouselPosition;
    }

    public void setCarouselPosition(Integer carouselPosition) {
        this.carouselPosition = carouselPosition;
    }

    public Boolean getExtended() {
        return extended;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public void setExtended(Boolean extended) {
        this.extended = extended;
    }

    public SeasonPackSettingsDTO getSeasonPackSettings() {
        return seasonPackSettingsDTO;
    }

    public void setSeasonPackSettings(SeasonPackSettingsDTO seasonPackSettingsDTO) {
        this.seasonPackSettingsDTO = seasonPackSettingsDTO;
    }

    public ProfessionalClientConditions getProfessionalClientConditions() {
        return professionalClientConditions;
    }

    public void setProfessionalClientConditions(ProfessionalClientConditions professionalClientConditions) {
        this.professionalClientConditions = professionalClientConditions;
    }

    public Boolean getPhoneValidationRequired() {
        return phoneValidationRequired;
    }

    public void setPhoneValidationRequired(Boolean phoneValidationRequired) {
        this.phoneValidationRequired = phoneValidationRequired;
    }

    public Boolean getAttendantVerificationRequired() {
        return attendantVerificationRequired;
    }

    public void setAttendantVerificationRequired(Boolean attendantVerificationRequired) {
        this.attendantVerificationRequired = attendantVerificationRequired;
    }

    public ChannelEventPostBookingQuestions getPostBookingQuestions() {
        return postBookingQuestions;
    }

    public void setPostBookingQuestions(ChannelEventPostBookingQuestions postBookingQuestions) {
        this.postBookingQuestions = postBookingQuestions;
    }

    public TourInfoDTO getTour() {
        return tour;
    }

    public void setTour(TourInfoDTO tour) {
        this.tour = tour;
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
