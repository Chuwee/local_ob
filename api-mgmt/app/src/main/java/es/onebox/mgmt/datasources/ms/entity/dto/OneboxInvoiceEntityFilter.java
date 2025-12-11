package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;
import java.util.List;

public class OneboxInvoiceEntityFilter implements Serializable {

    private static final long serialVersionUID = 2L;

    private String name;
    private List<Long> entityIds;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(List<Long> entityIds) {
        this.entityIds = entityIds;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
