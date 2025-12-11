package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;
import java.util.List;

public class RenewalCandidatesSeasonTicket implements Serializable {

    private static final long serialVersionUID = -7183443480568139940L;
    private Long id;
    private String name;
    private Boolean compatible;
    private List<RenewalCandidateReasonSeasonTicket> reasons;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompatible() {
        return compatible;
    }

    public void setCompatible(Boolean compatible) {
        this.compatible = compatible;
    }

    public List<RenewalCandidateReasonSeasonTicket> getReasons() {
        return reasons;
    }

    public void setReasons(List<RenewalCandidateReasonSeasonTicket> reasons) {
        this.reasons = reasons;
    }
}

