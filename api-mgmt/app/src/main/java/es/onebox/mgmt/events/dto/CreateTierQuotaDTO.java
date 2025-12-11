package es.onebox.mgmt.events.dto;

import java.io.Serializable;

public class CreateTierQuotaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer limit;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
