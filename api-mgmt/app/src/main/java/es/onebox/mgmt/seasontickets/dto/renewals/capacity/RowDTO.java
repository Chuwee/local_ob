package es.onebox.mgmt.seasontickets.dto.renewals.capacity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class RowDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("row_id")
    private Long rowId;
    @JsonProperty("row_name")
    private String rowName;
    @JsonProperty("available_seats")
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
