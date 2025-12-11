package es.onebox.event.catalog.elasticsearch.dto.channelpack;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrix;
import es.onebox.event.packs.enums.PackPricingType;
import es.onebox.event.packs.enums.PackRangeType;
import es.onebox.event.packs.enums.PackStatus;
import es.onebox.event.packs.enums.PackSubtype;
import es.onebox.event.packs.enums.PackType;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@CouchDocument
public class ChannelPack implements Serializable {

    @Serial
    private static final long serialVersionUID = -223660303426417272L;

    @Id(index = 2)
    private Long id;
    @Id(index = 1)
    private Long channelId;
    private String name;
    private PackType type;
    private PackSubtype subtype;
    private Boolean soldOut;
    private Boolean forSale;
    private Boolean onSale;
    private Long promotionId;
    private PackPricingType pricingType;
    private PackRangeType packRangeType;
    private ZonedDateTime customStartSaleDate;
    private ZonedDateTime customEndSaleDate;
    private Boolean unifiedPrice;
    private Boolean showDate;
    private Boolean showDateTime;
    private Boolean showMainVenue;
    private Boolean showMainDate;
    private ChannelPackDates dates;
    private PackStatus status;
    private List<ChannelPackItem> items;
    private List<ChannelPackCommElement> communicationElements;
    private Boolean showUnconfirmedDate;
    private String customCategoryCode;
    private Boolean suggested;
    private Boolean onSaleForLoggedUsers;
    private PriceMatrix prices;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PackType getType() {
        return type;
    }

    public void setType(PackType type) {
        this.type = type;
    }

    public PackSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(PackSubtype subtype) {
        this.subtype = subtype;
    }

    public Boolean getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    public Boolean getForSale() {
        return forSale;
    }

    public void setForSale(Boolean forSale) {
        this.forSale = forSale;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public PackPricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PackPricingType pricingType) {
        this.pricingType = pricingType;
    }

    public PackRangeType getPackRangeType() {
        return packRangeType;
    }

    public void setPackRangeType(PackRangeType packRangeType) {
        this.packRangeType = packRangeType;
    }

    public ZonedDateTime getCustomStartSaleDate() {
        return customStartSaleDate;
    }

    public void setCustomStartSaleDate(ZonedDateTime customStartSaleDate) {
        this.customStartSaleDate = customStartSaleDate;
    }

    public ZonedDateTime getCustomEndSaleDate() {
        return customEndSaleDate;
    }

    public void setCustomEndSaleDate(ZonedDateTime customEndSaleDate) {
        this.customEndSaleDate = customEndSaleDate;
    }

    public Boolean getUnifiedPrice() {
        return unifiedPrice;
    }

    public void setUnifiedPrice(Boolean unifiedPrice) {
        this.unifiedPrice = unifiedPrice;
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

    public Boolean getShowMainVenue() {
        return showMainVenue;
    }

    public void setShowMainVenue(Boolean showMainVenue) {
        this.showMainVenue = showMainVenue;
    }

    public Boolean getShowMainDate() {
        return showMainDate;
    }

    public void setShowMainDate(Boolean showMainDate) {
        this.showMainDate = showMainDate;
    }

    public ChannelPackDates getDates() {
        return dates;
    }

    public void setDates(ChannelPackDates dates) {
        this.dates = dates;
    }

    public PackStatus getStatus() {
        return status;
    }

    public void setStatus(PackStatus status) {
        this.status = status;
    }

    public List<ChannelPackItem> getItems() {
        return items;
    }

    public void setItems(List<ChannelPackItem> items) {
        this.items = items;
    }

    public List<ChannelPackCommElement> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<ChannelPackCommElement> communicationElements) {
        this.communicationElements = communicationElements;
    }

    public Boolean getShowUnconfirmedDate() {
        return showUnconfirmedDate;
    }

    public void setShowUnconfirmedDate(Boolean showUnconfirmedDate) {
        this.showUnconfirmedDate = showUnconfirmedDate;
    }

    public String getCustomCategoryCode() {
        return customCategoryCode;
    }

    public void setCustomCategoryCode(String customCategoryCode) {
        this.customCategoryCode = customCategoryCode;
    }

    public Boolean getSuggested() {
        return suggested;
    }

    public void setSuggested(Boolean suggested) {
        this.suggested = suggested;
    }

    public Boolean getOnSaleForLoggedUsers() {
        return onSaleForLoggedUsers;
    }

    public void setOnSaleForLoggedUsers(Boolean onSaleForLoggedUsers) {
        this.onSaleForLoggedUsers = onSaleForLoggedUsers;
    }

    public PriceMatrix getPrices() {
        return prices;
    }

    public void setPrices(PriceMatrix prices) {
        this.prices = prices;
    }
}
