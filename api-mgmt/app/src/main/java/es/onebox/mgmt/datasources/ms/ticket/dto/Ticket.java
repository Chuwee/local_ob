package es.onebox.mgmt.datasources.ms.ticket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.dal.dto.couch.enums.TicketType;
import es.onebox.mgmt.datasources.ms.ticket.enums.AccessibilityType;
import es.onebox.mgmt.datasources.ms.ticket.enums.VisibilityType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;
    @JsonProperty("seat_id")
    private Long seatId;
    @JsonProperty("status")
    private TicketStatus status;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("token")
    private String token;
    @JsonProperty("barcode")
    private String barcode;
    @JsonProperty("external_barcode")
    private String externalBarcode;
    @JsonProperty("external_id")
    private Long externalId;
    @JsonProperty("related_ticket_id")
    private Long relatedTicketId;
    @JsonProperty("ticket_group_id")
    private Long ticketGroupId;
    @JsonProperty("ticket_type")
    private TicketType ticketType;
    @JsonProperty("price_type_id")
    private Long priceTypeId;
    @JsonProperty("blocking_reason_id")
    private Long blockingReasonId;
    @JsonProperty("quota_id")
    private Long quotaId;
    @JsonProperty("channel_id")
    private Long channelId;

    @JsonProperty("session_id")
    private Long sessionId;
    @JsonProperty("season_ticket_id")
    private Long seasonTicketId;
    @JsonProperty("container")
    private Long container;
    @JsonProperty("sector_id")
    private Long sectorId;
    @JsonProperty("not_numbered_area_id")
    private Long notNumberedAreaId;
    @JsonProperty("row")
    private Long row;
    @JsonProperty("row_block")
    private String rowBlock;
    @JsonProperty("seat")
    private String seat;
    @JsonProperty("row_order")
    private Long rowOrder;
    @JsonProperty("weight")
    private Long weight;
    @JsonProperty("accessibility_type")
    private AccessibilityType accessibilityType;
    @JsonProperty("visibility_type")
    private VisibilityType visibilityType;

    @JsonProperty("gate_id")
    private Long gateId;
    @JsonProperty("activity_gate_id")
    private Long activityGateId;

    @JsonProperty("update_date")
    private ZonedDateTime updateDate;
    @JsonProperty("tstamp_system")
    private Long tstampSys;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getExternalBarcode() {
        return externalBarcode;
    }

    public void setExternalBarcode(String externalBarcode) {
        this.externalBarcode = externalBarcode;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public Long getRelatedTicketId() {
        return relatedTicketId;
    }

    public void setRelatedTicketId(Long relatedTicketId) {
        this.relatedTicketId = relatedTicketId;
    }

    public Long getTicketGroupId() {
        return ticketGroupId;
    }

    public void setTicketGroupId(Long ticketGroupId) {
        this.ticketGroupId = ticketGroupId;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public Long getBlockingReasonId() {
        return blockingReasonId;
    }

    public void setBlockingReasonId(Long blockingReasonId) {
        this.blockingReasonId = blockingReasonId;
    }

    public Long getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Long quotaId) {
        this.quotaId = quotaId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public Long getContainer() {
        return container;
    }

    public void setContainer(Long container) {
        this.container = container;
    }

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public Long getNotNumberedAreaId() {
        return notNumberedAreaId;
    }

    public void setNotNumberedAreaId(Long notNumberedAreaId) {
        this.notNumberedAreaId = notNumberedAreaId;
    }

    public Long getRow() {
        return row;
    }

    public void setRow(Long row) {
        this.row = row;
    }

    public String getRowBlock() {
        return rowBlock;
    }

    public void setRowBlock(String rowBlock) {
        this.rowBlock = rowBlock;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public Long getRowOrder() {
        return rowOrder;
    }

    public void setRowOrder(Long rowOrder) {
        this.rowOrder = rowOrder;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public AccessibilityType getAccessibilityType() {
        return accessibilityType;
    }

    public void setAccessibilityType(AccessibilityType accessibilityType) {
        this.accessibilityType = accessibilityType;
    }

    public VisibilityType getVisibilityType() {
        return visibilityType;
    }

    public void setVisibilityType(VisibilityType visibilityType) {
        this.visibilityType = visibilityType;
    }

    public Long getGateId() {
        return gateId;
    }

    public void setGateId(Long gateId) {
        this.gateId = gateId;
    }

    public Long getActivityGateId() {
        return activityGateId;
    }

    public void setActivityGateId(Long activityGateId) {
        this.activityGateId = activityGateId;
    }

    public ZonedDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(ZonedDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Long getTstampSys() {
        return tstampSys;
    }

    public void setTstampSys(Long tstampSys) {
        this.tstampSys = tstampSys;
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
