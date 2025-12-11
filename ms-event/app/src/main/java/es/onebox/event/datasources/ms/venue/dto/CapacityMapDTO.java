package es.onebox.event.datasources.ms.venue.dto;

import java.io.Serializable;
import java.util.List;

public class CapacityMapDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<SectorMapCapacitiesDTO> sectorMap;
    private List<RowCapacityDTO> rows;

    public List<SectorMapCapacitiesDTO> getSectorMap() {
        return sectorMap;
    }

    public void setSectorMap(List<SectorMapCapacitiesDTO> sectorMap) {
        this.sectorMap = sectorMap;
    }

    public List<RowCapacityDTO> getRows() {
        return rows;
    }

    public void setRows(List<RowCapacityDTO> rows) {
        this.rows = rows;
    }
}
