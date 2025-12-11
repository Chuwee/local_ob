package es.onebox.mgmt.common.auth.dto;

public class MaxMembersDTO {

    private Boolean enabled;
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
