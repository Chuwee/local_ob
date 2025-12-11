package es.onebox.event.catalog.dto.venue.container;

import io.r2dbc.spi.Parameter;

import java.io.Serial;
import java.io.Serializable;

public class VenueContainerSeat extends VenueContainerItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 7963866819704243524L;

    private String num;
    private Integer sectorId;
    private Integer rowId;
    private String rowName;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Integer getSectorId() {
        return sectorId;
    }

    public void setSectorId(Integer sectorId) {
        this.sectorId = sectorId;
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
}
