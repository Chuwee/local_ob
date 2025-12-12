package es.onebox.circuitcat.conciliation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.circuitcat.common.dto.Seat;

import java.util.List;

public class ConciliationDTO {
    private List<Seat> inconsistencies;
    @JsonProperty("internal_members")
    private List<Seat> internalMembers;
    @JsonProperty("external_members")
    private List<Seat> externalMembers;

    public List<Seat> getInconsistencies() {
        return inconsistencies;
    }

    public void setInconsistencies(List<Seat> inconsistencies) {
        this.inconsistencies = inconsistencies;
    }

    public List<Seat> getInternalMembers() {
        return internalMembers;
    }

    public void setInternalMembers(List<Seat> internalMembers) {
        this.internalMembers = internalMembers;
    }

    public List<Seat> getExternalMembers() {
        return externalMembers;
    }

    public void setExternalMembers(List<Seat> externalMembers) {
        this.externalMembers = externalMembers;
    }
}
