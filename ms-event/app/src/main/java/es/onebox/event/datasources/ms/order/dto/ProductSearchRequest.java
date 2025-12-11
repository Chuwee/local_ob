package es.onebox.event.datasources.ms.order.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.dal.dto.common.enums.EventType;
import es.onebox.dal.dto.couch.enums.OrderState;
import es.onebox.dal.dto.couch.enums.OrderType;
import es.onebox.dal.dto.couch.enums.PrintsFilter;
import es.onebox.dal.dto.couch.enums.ProductType;
import es.onebox.dal.dto.couch.enums.SeasonProductReleaseStatus;
import es.onebox.dal.dto.couch.enums.SeatType;
import es.onebox.dal.dto.couch.enums.TicketType;
import es.onebox.dal.dto.couch.enums.TicketVisibility;
import es.onebox.dal.dto.couch.enums.ValidationsFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

@MaxLimit(1000)
public class ProductSearchRequest extends BaseRequestFilter {

   @Serial
    private static final long serialVersionUID = 1L;

    private Long eventOperatorId;
    private Long channelOperatorId;
    private Long entityAdminId;
    private Long eventOwnerId;
    private Long channelOwnerId;
    private Long venueOwnerId;

    private List<Long> ids;
    private List<String> productBarcodes;
    private List<String> orderCodes;
    private List<String> orderTransactionCodes;
    private List<Long> channelIds;
    private List<Long> channelEntityIds;
    private List<String> customerIds;
    private List<OrderType> orderTypes;
    private List<OrderState> orderStates;
    private List<OrderType> notOrderTypes;
    private List<OrderType> ticketStates;
    private SearchMode searchMode = SearchMode.NET;
    private List<Long> eventEntityIds;
    private List<Long> eventIds;
    private List<Long> sessionIds;
    private List<Long> productIds;
    private List<Long> variantIds;
    private List<Long> venueIds;
    private List<Long> priceZoneIds;
    private List<Long> sectorIds;
    private List<String> buyerEmail;
    private List<EventType> eventTypes;
    private List<TicketType> ticketTypes;
    private List<SeatType> seatTypes;
    private List<ProductType> productTypes;
    private ZonedDateTime purchaseDateFrom;
    private ZonedDateTime purchaseDateTo;
    private List<FilterWithOperator<ZonedDateTime>> lastModified;
    private ClientType clientType;
    private List<Long> clientEntityIds;
    private List<Long> clientUserIds;
    private Boolean productSold;
    private Boolean productRefunded;
    private Boolean productReallocated;
    private Boolean productModifiedByBooking;
    private ZonedDateTime sessionStartDateFrom;
    private ZonedDateTime sessionStartDateTo;
    private Boolean includeAggs;
    private PrintsFilter print;
    private ValidationsFilter validation;
    private String q;
    private SortOperator<ProductSortableField> sort;
    private List<TicketVisibility> visibility;
    private String currencyCode;
    private List<SeasonProductReleaseStatus> releaseStatus;

    public Long getEventOperatorId() {
        return eventOperatorId;
    }

    public void setEventOperatorId(Long eventOperatorId) {
        this.eventOperatorId = eventOperatorId;
    }

    public Long getChannelOperatorId() {
        return channelOperatorId;
    }

    public void setChannelOperatorId(Long channelOperatorId) {
        this.channelOperatorId = channelOperatorId;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public Long getEventOwnerId() {
        return eventOwnerId;
    }

    public void setEventOwnerId(Long eventOwnerId) {
        this.eventOwnerId = eventOwnerId;
    }

    public Long getChannelOwnerId() {
        return channelOwnerId;
    }

    public void setChannelOwnerId(Long channelOwnerId) {
        this.channelOwnerId = channelOwnerId;
    }

    public Long getVenueOwnerId() {
        return venueOwnerId;
    }

    public void setVenueOwnerId(Long venueOwnerId) {
        this.venueOwnerId = venueOwnerId;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public List<String> getProductBarcodes() {
        return productBarcodes;
    }

    public void setProductBarcodes(List<String> productBarcodes) {
        this.productBarcodes = productBarcodes;
    }

    public List<String> getOrderCodes() {
        return orderCodes;
    }

    public void setOrderCodes(List<String> orderCodes) {
        this.orderCodes = orderCodes;
    }

    public List<String> getOrderTransactionCodes() {
        return orderTransactionCodes;
    }

    public void setOrderTransactionCodes(List<String> orderTransactionCodes) {
        this.orderTransactionCodes = orderTransactionCodes;
    }

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public List<Long> getChannelEntityIds() {
        return channelEntityIds;
    }

    public void setChannelEntityIds(List<Long> channelEntityIds) {
        this.channelEntityIds = channelEntityIds;
    }

    public List<String> getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(List<String> customerIds) {
        this.customerIds = customerIds;
    }

    public List<OrderType> getOrderTypes() {
        return orderTypes;
    }

    public void setOrderTypes(List<OrderType> orderTypes) {
        this.orderTypes = orderTypes;
    }

    public List<OrderState> getOrderStates() {
        return orderStates;
    }

    public void setOrderStates(List<OrderState> orderStates) {
        this.orderStates = orderStates;
    }

    public List<OrderType> getNotOrderTypes() {
        return notOrderTypes;
    }

    public void setNotOrderTypes(List<OrderType> notOrderTypes) {
        this.notOrderTypes = notOrderTypes;
    }

    public List<OrderType> getTicketStates() {
        return ticketStates;
    }

    public void setTicketStates(List<OrderType> ticketStates) {
        this.ticketStates = ticketStates;
    }

    public SearchMode getSearchMode() {
        return searchMode;
    }

    public ProductSearchRequest setSearchMode(SearchMode searchMode) {
        this.searchMode = searchMode;
        return this;
    }

    public List<Long> getEventEntityIds() {
        return eventEntityIds;
    }

    public void setEventEntityIds(List<Long> eventEntityIds) {
        this.eventEntityIds = eventEntityIds;
    }

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    public List<Long> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public List<Long> getProductIds() { return productIds; }

    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }

    public List<Long> getVariantIds() { return variantIds; }

    public void setVariantIds(List<Long> variantIds) { this.variantIds = variantIds; }

    public List<Long> getVenueIds() {
        return venueIds;
    }

    public void setVenueIds(List<Long> venueIds) {
        this.venueIds = venueIds;
    }

    public List<Long> getPriceZoneIds() {
        return priceZoneIds;
    }

    public void setPriceZoneIds(List<Long> priceZoneIds) {
        this.priceZoneIds = priceZoneIds;
    }

    public List<Long> getSectorIds() {
        return sectorIds;
    }

    public void setSectorIds(List<Long> sectorIds) {
        this.sectorIds = sectorIds;
    }

    public List<String> getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(List<String> buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public List<EventType> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<EventType> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public List<TicketType> getTicketTypes() {
        return ticketTypes;
    }

    public void setTicketTypes(List<TicketType> ticketTypes) {
        this.ticketTypes = ticketTypes;
    }

    public List<SeatType> getSeatTypes() {
        return seatTypes;
    }

    public void setSeatTypes(List<SeatType> seatTypes) {
        this.seatTypes = seatTypes;
    }

    public List<ProductType> getProductTypes() {
        return productTypes;
    }

    public void setProductTypes(List<ProductType> productTypes) {
        this.productTypes = productTypes;
    }

    public ZonedDateTime getPurchaseDateFrom() {
        return purchaseDateFrom;
    }

    public void setPurchaseDateFrom(ZonedDateTime purchaseDateFrom) {
        this.purchaseDateFrom = purchaseDateFrom;
    }

    public ZonedDateTime getPurchaseDateTo() {
        return purchaseDateTo;
    }

    public void setPurchaseDateTo(ZonedDateTime purchaseDateTo) {
        this.purchaseDateTo = purchaseDateTo;
    }

    public List<FilterWithOperator<ZonedDateTime>> getLastModified() {
        return lastModified;
    }

    public void setLastModified(List<FilterWithOperator<ZonedDateTime>> lastModified) {
        this.lastModified = lastModified;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public List<Long> getClientEntityIds() {
        return clientEntityIds;
    }

    public void setClientEntityIds(List<Long> clientEntityIds) {
        this.clientEntityIds = clientEntityIds;
    }

    public List<Long> getClientUserIds() {
        return clientUserIds;
    }

    public void setClientUserIds(List<Long> clientUserIds) {
        this.clientUserIds = clientUserIds;
    }

    public Boolean getProductSold() {
        return productSold;
    }

    public void setProductSold(Boolean productSold) {
        this.productSold = productSold;
    }

    public Boolean getProductRefunded() {
        return productRefunded;
    }

    public void setProductRefunded(Boolean productRefunded) {
        this.productRefunded = productRefunded;
    }

    public Boolean getProductReallocated() {
        return productReallocated;
    }

    public void setProductReallocated(Boolean productReallocated) {
        this.productReallocated = productReallocated;
    }

    public Boolean getProductModifiedByBooking() {
        return productModifiedByBooking;
    }

    public void setProductModifiedByBooking(Boolean productModifiedByBooking) {
        this.productModifiedByBooking = productModifiedByBooking;
    }

    public ZonedDateTime getSessionStartDateFrom() {
        return sessionStartDateFrom;
    }

    public void setSessionStartDateFrom(ZonedDateTime sessionStartDateFrom) {
        this.sessionStartDateFrom = sessionStartDateFrom;
    }

    public ZonedDateTime getSessionStartDateTo() {
        return sessionStartDateTo;
    }

    public void setSessionStartDateTo(ZonedDateTime sessionStartDateTo) {
        this.sessionStartDateTo = sessionStartDateTo;
    }

    public Boolean getIncludeAggs() {
        return includeAggs;
    }

    public void setIncludeAggs(Boolean includeAggs) {
        this.includeAggs = includeAggs;
    }

    public PrintsFilter getPrint() {
        return print;
    }

    public void setPrint(PrintsFilter print) {
        this.print = print;
    }

    public ValidationsFilter getValidation() {
        return validation;
    }

    public void setValidation(ValidationsFilter validation) {
        this.validation = validation;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public SortOperator<ProductSortableField> getSort() {
        return sort;
    }

    public void setSort(SortOperator<ProductSortableField> sort) {
        this.sort = sort;
    }

    public List<TicketVisibility> getVisibility() {
        return visibility;
    }

    public void setVisibility(List<TicketVisibility> visibility) {
        this.visibility = visibility;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<SeasonProductReleaseStatus> getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(List<SeasonProductReleaseStatus> releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}