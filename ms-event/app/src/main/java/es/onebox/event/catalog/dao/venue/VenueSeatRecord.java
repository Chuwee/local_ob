package es.onebox.event.catalog.dao.venue;

import java.io.Serial;
import java.io.Serializable;

public class VenueSeatRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1955826880094057140L;

    private Long id;
    private Integer containerId;
    private String num;
    private Integer rowId;
    private String rowName;
    private Integer sectorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getContainerId() {
        return containerId;
    }

    public void setContainerId(Integer containerId) {
        this.containerId = containerId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
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
}
