package es.onebox.mgmt.sessions.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public class SessionPackSeatLinkDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ids is mandatory")
    private List<Long> ids;

    private String target;

    private Long quota;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Long getQuota() {
        return quota;
    }

    public void setQuota(Long quota) {
        this.quota = quota;
    }
}
