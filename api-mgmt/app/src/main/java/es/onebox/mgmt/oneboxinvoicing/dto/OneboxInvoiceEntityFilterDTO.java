package es.onebox.mgmt.oneboxinvoicing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class OneboxInvoiceEntityFilterDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    private String name;

    @JsonProperty("entity_ids")
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
