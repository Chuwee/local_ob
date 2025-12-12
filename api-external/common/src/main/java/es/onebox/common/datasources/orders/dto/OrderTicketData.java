package es.onebox.common.datasources.orders.dto;

import es.onebox.common.datasources.orderitems.enums.TicketType;
import es.onebox.common.datasources.orders.enums.SeatType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class OrderTicketData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //Ticket specific fields
    private Long seatId;
    private String numSeat;
    private Integer rowId;
    private String rowName;
    private Integer sectorId;
    private String sectorName;
    private Integer notNumberedAreaId;
    private String notNumberedAreaName;
    private Long priceZoneId;
    private String priceZoneName;
    private Integer viewId;
    private Integer accessId;
    private String accessName;
    private Integer quotaId;
    private Integer rateId;

    //Avet specific fields
    private String externalAccess;
    private String externalEntrance;
    private String externalGate;
    private String externalZone;
    private Boolean isExternalTicket;

    private String rowBlock;
    private Integer rowOrder;

    private SeatType seatType;
    private TicketType ticketType;

    //EYE-DANGER! This barcode is not very reliable due to season sessions
    private String barcode;
    private String externalBarcode;

    private ZonedDateTime barcodesLastUpdate;

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getNumSeat() {
        return numSeat;
    }

    public void setNumSeat(String numSeat) {
        this.numSeat = numSeat;
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public Integer getSectorId() {
        return sectorId;
    }

    public void setSectorId(Integer sectorId) {
        this.sectorId = sectorId;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public Integer getNotNumberedAreaId() {
        return notNumberedAreaId;
    }

    public void setNotNumberedAreaId(Integer notNumberedAreaId) {
        this.notNumberedAreaId = notNumberedAreaId;
    }

    public String getNotNumberedAreaName() {
        return notNumberedAreaName;
    }

    public void setNotNumberedAreaName(String notNumberedAreaName) {
        this.notNumberedAreaName = notNumberedAreaName;
    }

    public Long getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(Long priceZoneId) {
        this.priceZoneId = priceZoneId;
    }

    public String getPriceZoneName() {
        return priceZoneName;
    }

    public void setPriceZoneName(String priceZoneName) {
        this.priceZoneName = priceZoneName;
    }

    public Integer getViewId() {
        return viewId;
    }

    public void setViewId(Integer viewId) {
        this.viewId = viewId;
    }

    public Integer getAccessId() {
        return accessId;
    }

    public void setAccessId(Integer accessId) {
        this.accessId = accessId;
    }

    public String getAccessName() {
        return accessName;
    }

    public void setAccessName(String accessName) {
        this.accessName = accessName;
    }

    public Integer getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Integer quotaId) {
        this.quotaId = quotaId;
    }

    public Integer getRateId() {
        return rateId;
    }

    public void setRateId(Integer rateId) {
        this.rateId = rateId;
    }

    public String getExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(String externalAccess) {
        this.externalAccess = externalAccess;
    }

    public String getExternalEntrance() {
        return externalEntrance;
    }

    public void setExternalEntrance(String externalEntrance) {
        this.externalEntrance = externalEntrance;
    }

    public String getExternalGate() {
        return externalGate;
    }

    public void setExternalGate(String externalGate) {
        this.externalGate = externalGate;
    }

    public String getExternalZone() {
        return externalZone;
    }

    public void setExternalZone(String externalZone) {
        this.externalZone = externalZone;
    }

    public Boolean getIsExternalTicket() {
        return isExternalTicket;
    }

    public void setIsExternalTicket(Boolean externalTicket) {
        isExternalTicket = externalTicket;
    }

    public String getRowBlock() {
        return rowBlock;
    }

    public void setRowBlock(String rowBlock) {
        this.rowBlock = rowBlock;
    }

    public Integer getRowOrder() {
        return rowOrder;
    }

    public void setRowOrder(Integer rowOrder) {
        this.rowOrder = rowOrder;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
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

    public ZonedDateTime getBarcodesLastUpdate() {
        return barcodesLastUpdate;
    }

    public void setBarcodesLastUpdate(ZonedDateTime barcodesLastUpdate) {
        this.barcodesLastUpdate = barcodesLastUpdate;
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
