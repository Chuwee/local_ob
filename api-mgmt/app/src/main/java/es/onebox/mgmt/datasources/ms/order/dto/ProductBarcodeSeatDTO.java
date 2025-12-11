package es.onebox.mgmt.datasources.ms.order.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.dal.dto.couch.enums.SeatType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ProductBarcodeSeatDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SeatType type;
    private IdNameDTO access;
    private IdNameDTO sector;
    private IdNameDTO row;
    private IdNameDTO notNumberedArea;
    private IdNameDTO seat;

    public SeatType getType() {
        return type;
    }

    public void setType(SeatType type) {
        this.type = type;
    }

    public IdNameDTO getAccess() {
        return access;
    }

    public void setAccess(IdNameDTO access) {
        this.access = access;
    }

    public IdNameDTO getSector() {
        return sector;
    }

    public void setSector(IdNameDTO sector) {
        this.sector = sector;
    }

    public IdNameDTO getRow() {
        return row;
    }

    public void setRow(IdNameDTO row) {
        this.row = row;
    }

    public IdNameDTO getNotNumberedArea() {
        return notNumberedArea;
    }

    public void setNotNumberedArea(IdNameDTO notNumberedArea) {
        this.notNumberedArea = notNumberedArea;
    }

    public IdNameDTO getSeat() {
        return seat;
    }

    public void setSeat(IdNameDTO seat) {
        this.seat = seat;
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
