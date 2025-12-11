package es.onebox.mgmt.datasources.ms.ticket.dto.availability;

import java.io.Serializable;

public class Row implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long rowId;
    private String rowName;
    private Long availableSeats;

    public Long getRowId() {
        return rowId;
    }

    public void setRowId(Long rowId) {
        this.rowId = rowId;
    }

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public Long getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Long availableSeats) {
        this.availableSeats = availableSeats;
    }
}
