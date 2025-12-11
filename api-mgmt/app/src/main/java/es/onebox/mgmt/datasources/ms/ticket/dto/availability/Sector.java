package es.onebox.mgmt.datasources.ms.ticket.dto.availability;

import java.io.Serializable;
import java.util.List;

public class Sector implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long sectorId;
    private String sectorName;
    private Long availableSeats;
    private List<Row> rowList;
    private List<NotNumberedZone> notNumberedZones;

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

    public List<Row> getRowList() {
        return rowList;
    }

    public void setRowList(List<Row> rowList) {
        this.rowList = rowList;
    }

    public List<NotNumberedZone> getNotNumberedZones() {
        return notNumberedZones;
    }

    public void setNotNumberedZones(List<NotNumberedZone> notNumberedZones) {
        this.notNumberedZones = notNumberedZones;
    }
}
