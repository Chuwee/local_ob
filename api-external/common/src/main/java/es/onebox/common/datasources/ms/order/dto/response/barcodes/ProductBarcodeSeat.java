package es.onebox.common.datasources.ms.order.dto.response.barcodes;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;

public class ProductBarcodeSeat implements Serializable {

    @Serial
    private static final long serialVersionUID = 7324805398033114242L;

    private IdNameDTO access;
    private IdNameCodeDTO sector;
    private IdNameDTO row;
    private IdNameDTO notNumberedArea;
    private IdNameDTO seat;

    public IdNameDTO getAccess() {
        return access;
    }

    public void setAccess(IdNameDTO access) {
        this.access = access;
    }

    public IdNameCodeDTO getSector() {
        return sector;
    }

    public void setSector(IdNameCodeDTO sector) {
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
}
