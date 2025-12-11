package es.onebox.event.catalog.elasticsearch.dto.channelpack;

import es.onebox.event.catalog.dto.CatalogCommunicationElementDTO;
import es.onebox.event.events.dto.EventWhitelabelSettingsDTO;
import es.onebox.event.packs.dto.PriceTypeRange;
import es.onebox.event.packs.enums.PackItemType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChannelPackItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 5553824240649464486L;

    // Common
    private Long itemId;
    private PackItemType type;
    private Boolean isMain;
    private String name;
    private String defaultLanguage;
    private ChannelPackItemDates dates;
    private List<CatalogCommunicationElementDTO> communicationElements;
    private Boolean displayItemInChannels;
    private Double informativePrice;

    // Events
    private Integer venueId;
    private Integer venueTemplateId;
    private EventWhitelabelSettingsDTO eventWhitelabelSettings;
    private ChannelPackNextEventSessionDTO nextSession;
    private List<Long> sessionsFilter;

    // Sessions
    private Boolean useCaptcha;
    private Integer priceTypeId;
    private Map<Integer, List<Integer>> priceTypeMapping;

    // Sessions and events
    private ChannelPackItemVenueDTO venue;
    private PriceTypeRange priceTypeRange;
    private Set<Integer> priceTypes;
    private Boolean showDate;
    private Boolean showDateTime;
    private Boolean showUnconfirmedDate;

    // Products
    private String deliveryPointName;
    private Integer variantId;
    private Integer deliveryPointId;
    private Boolean sharedBarcode;
    private Boolean hideDeliveryPoint;
    private Boolean hideDeliveryDateTime;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public PackItemType getType() {
        return type;
    }

    public void setType(PackItemType type) {
        this.type = type;
    }

    public Boolean getMain() {
        return isMain;
    }

    public void setMain(Boolean main) {
        isMain = main;
    }

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public Integer getVariantId() {
        return variantId;
    }

    public Integer getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Integer venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    public Integer getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Integer priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public Map<Integer, List<Integer>> getPriceTypeMapping() {
        return priceTypeMapping;
    }

    public void setPriceTypeMapping(Map<Integer, List<Integer>> priceTypeMapping) {
        this.priceTypeMapping = priceTypeMapping;
    }

    public Integer getDeliveryPointId() {
        return deliveryPointId;
    }

    public void setDeliveryPointId(Integer deliveryPointId) {
        this.deliveryPointId = deliveryPointId;
    }

    public Boolean getSharedBarcode() {
        return sharedBarcode;
    }

    public void setSharedBarcode(Boolean sharedBarcode) {
        this.sharedBarcode = sharedBarcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChannelPackItemDates getDates() {
        return dates;
    }

    public void setDates(ChannelPackItemDates date) {
        this.dates = date;
    }

    public ChannelPackItemVenueDTO getVenue() {
        return venue;
    }

    public void setVenue(ChannelPackItemVenueDTO venue) {
        this.venue = venue;
    }

    public List<CatalogCommunicationElementDTO> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<CatalogCommunicationElementDTO> communicationElements) {
        this.communicationElements = communicationElements;
    }

    public Boolean getDisplayItemInChannels() {
        return displayItemInChannels;
    }

    public void setDisplayItemInChannels(Boolean displayItemInChannels) {
        this.displayItemInChannels = displayItemInChannels;
    }

    public Double getInformativePrice() {
        return informativePrice;
    }

    public void setInformativePrice(Double informativePrice) {
        this.informativePrice = informativePrice;
    }

    public PriceTypeRange getPriceTypeRange() {
        return priceTypeRange;
    }

    public void setPriceTypeRange(PriceTypeRange priceTypeRange) {
        this.priceTypeRange = priceTypeRange;
    }

    public Set<Integer> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(Set<Integer> priceTypes) {
        this.priceTypes = priceTypes;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public Boolean getShowDate() {
        return showDate;
    }

    public void setShowDate(Boolean showDate) {
        this.showDate = showDate;
    }

    public Boolean getShowDateTime() {
        return showDateTime;
    }

    public void setShowDateTime(Boolean showDateTime) {
        this.showDateTime = showDateTime;
    }

    public Boolean getShowUnconfirmedDate() {
        return showUnconfirmedDate;
    }

    public void setShowUnconfirmedDate(Boolean showUnconfirmedDate) {
        this.showUnconfirmedDate = showUnconfirmedDate;
    }

    public Boolean getUseCaptcha() {
        return useCaptcha;
    }

    public void setUseCaptcha(Boolean useCaptcha) {
        this.useCaptcha = useCaptcha;
    }

    public EventWhitelabelSettingsDTO getEventWhitelabelSettings() {
        return eventWhitelabelSettings;
    }

    public void setEventWhitelabelSettings(EventWhitelabelSettingsDTO eventWhitelabelSettings) {
        this.eventWhitelabelSettings = eventWhitelabelSettings;
    }

    public ChannelPackNextEventSessionDTO getNextSession() {
        return nextSession;
    }

    public void setNextSession(ChannelPackNextEventSessionDTO nextSession) {
        this.nextSession = nextSession;
    }

    public List<Long> getSessionsFilter() {
        return sessionsFilter;
    }

    public void setSessionsFilter(List<Long> sessionsFilter) {
        this.sessionsFilter = sessionsFilter;
    }


    public String getDeliveryPointName() {
        return deliveryPointName;
    }

    public void setDeliveryPointName(String deliveryPointName) {
        this.deliveryPointName = deliveryPointName;
    }

    public Boolean getHideDeliveryPoint() {
        return hideDeliveryPoint;
    }

    public void setHideDeliveryPoint(Boolean hideDeliveryPoint) {
        this.hideDeliveryPoint = hideDeliveryPoint;
    }

    public Boolean getHideDeliveryDateTime() {
        return hideDeliveryDateTime;
    }

    public void setHideDeliveryDateTime(Boolean hideDeliveryDateTime) {
        this.hideDeliveryDateTime = hideDeliveryDateTime;
    }
}
