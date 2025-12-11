package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;

public class Tax implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long detailId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDetailId() { return detailId; }

    public void setDetailId(Long detailId) { this.detailId = detailId; }
}
