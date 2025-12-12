package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class OrderTicketDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long seatId;
    private String numSeat;
    private Integer rowId;
    private String rowName;
    private Integer sectorId;
    private String sectorCode;
    private String sectorName;
    private Integer notNumberedAreaId;
    private String notNumberedAreaName;
    private Integer priceZoneId;
    private String priceZoneName;
    private String priceZoneCode;
    private Integer viewId;
    private Integer accessId;
    private String accessName;
    private Integer quotaId;
    private Integer rateId;
    private String externalAccess;
    private String externalAccessTime;
    private String externalEntrance;
    private String externalGate;
    private String externalZone;
    private String externalRound;
    private String externalSeatId;
    private Boolean isExternalTicket;
    private String rowBlock;
    private Integer rowOrder;
    private TicketAccesibility accessibility;
    private TicketVisibility visibility;
    private SeatType seatType;
    private TicketType ticketType;
    private String barcode;
    private List<TicketBarcodeStatus> barcodes;
    private String externalBarcode;
    private ZonedDateTime barcodesLastUpdate;
    private SeatBuyerDataDTO seatBuyerData;
    private Map<String, Object> externalDataProperties;

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

    public String getSectorCode() {
        return sectorCode;
    }

    public void setSectorCode(String sectorCode) {
        this.sectorCode = sectorCode;
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

    public Integer getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(Integer priceZoneId) {
        this.priceZoneId = priceZoneId;
    }

    public String getPriceZoneName() {
        return priceZoneName;
    }

    public void setPriceZoneName(String priceZoneName) {
        this.priceZoneName = priceZoneName;
    }

    public String getPriceZoneCode() {
        return priceZoneCode;
    }

    public void setPriceZoneCode(String priceZoneCode) {
        this.priceZoneCode = priceZoneCode;
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

    public String getExternalAccessTime() {
        return externalAccessTime;
    }

    public void setExternalAccessTime(String externalAccessTime) {
        this.externalAccessTime = externalAccessTime;
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

    public String getExternalRound() {
        return externalRound;
    }

    public void setExternalRound(String externalRound) {
        this.externalRound = externalRound;
    }

    public String getExternalSeatId() {
        return externalSeatId;
    }

    public void setExternalSeatId(String externalSeatId) {
        this.externalSeatId = externalSeatId;
    }

    public Boolean getExternalTicket() {
        return isExternalTicket;
    }

    public void setExternalTicket(Boolean externalTicket) {
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

    public TicketAccesibility getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(TicketAccesibility accessibility) {
        this.accessibility = accessibility;
    }

    public TicketVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(TicketVisibility visibility) {
        this.visibility = visibility;
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

    public List<TicketBarcodeStatus> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<TicketBarcodeStatus> barcodes) {
        this.barcodes = barcodes;
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

    public SeatBuyerDataDTO getSeatBuyerData() {
        return seatBuyerData;
    }

    public void setSeatBuyerData(SeatBuyerDataDTO seatBuyerData) {
        this.seatBuyerData = seatBuyerData;
    }

    public Map<String, Object> getExternalDataProperties() {
        return externalDataProperties;
    }

    public void setExternalDataProperties(Map<String, Object> externalDataProperties) {
        this.externalDataProperties = externalDataProperties;
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
