package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class BarcodeDTO<T extends Enum<T>> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String barcode;
    private String locator;
    private T status;
    private IdNameDTO event;
    private BarcodeSessionDataDTO session;
    @JsonProperty("price_zone")
    private IdNameDTO priceZone;
    @JsonProperty("seat_data")
    private SeatDataDTO seatData;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public T getStatus() {
        return status;
    }

    public void setStatus(T status) {
        this.status = status;
    }

    public IdNameDTO getEvent() {
        return event;
    }

    public void setEvent(IdNameDTO event) {
        this.event = event;
    }

    public BarcodeSessionDataDTO getSession() {
        return session;
    }

    public void setSession(BarcodeSessionDataDTO session) {
        this.session = session;
    }

    public IdNameDTO getPriceZone() {
        return priceZone;
    }

    public void setPriceZone(IdNameDTO priceZone) {
        this.priceZone = priceZone;
    }

    public SeatDataDTO getSeatData() {
        return seatData;
    }

    public void setSeatData(SeatDataDTO seatData) {
        this.seatData = seatData;
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
