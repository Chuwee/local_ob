package es.onebox.event.seasontickets.dto.renewals;

import java.io.Serializable;
import java.util.List;

public class RenewalCandidateSeasonTicketDTO implements Serializable {
    private static final long serialVersionUID = -4204567562089322275L;

    private Long id;
    private String name;
    private Boolean compatible;
    private List<RenewalCandidateReasonSeasonTicketDTO> reasons;

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

    public List<RenewalCandidateReasonSeasonTicketDTO> getReasons() {
        return reasons;
    }

    public void setReasons(List<RenewalCandidateReasonSeasonTicketDTO> reasons) {
        this.reasons = reasons;
    }
}
