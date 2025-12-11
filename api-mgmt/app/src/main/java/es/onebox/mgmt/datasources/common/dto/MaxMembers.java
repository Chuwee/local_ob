package es.onebox.mgmt.datasources.common.dto;


import jakarta.validation.constraints.Min;

public class MaxMembers {

    private Boolean enabled;
    @Min(1)
    private Integer limit;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

}
