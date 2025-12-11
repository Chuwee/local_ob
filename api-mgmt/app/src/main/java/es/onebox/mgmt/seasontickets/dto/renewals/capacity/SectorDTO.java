package es.onebox.mgmt.seasontickets.dto.renewals.capacity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class SectorDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("sector_id")
    private Long sectorId;
    @JsonProperty("sector_name")
    private String sectorName;
    @JsonProperty("available_seats")
    private Long availableSeats;
    @JsonProperty("rows")
    private List<RowDTO> rowList;
    @JsonProperty("not_numbered_zones")
    private List<NotNumberedZoneDTO> notNumberedZones;

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public Long getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Long availableSeats) {
        this.availableSeats = availableSeats;
    }

    public List<RowDTO> getRowList() {
        return rowList;
    }

    public void setRowList(List<RowDTO> rowList) {
        this.rowList = rowList;
    }

    public List<NotNumberedZoneDTO> getNotNumberedZones() {
        return notNumberedZones;
    }

    public void setNotNumberedZones(List<NotNumberedZoneDTO> notNumberedZones) {
        this.notNumberedZones = notNumberedZones;
    }
}
