package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.externalconfiguration.enums.CapacityNameType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ClubConfigCapacityDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5598054028746542253L;

    @JsonProperty("capacity_name_type")
    private CapacityNameType capacityNameType;

    @JsonProperty("capacities")
    private List<Integer> capacities;

    @JsonProperty("season")
    private String season;

    @JsonProperty("members_capacity_id")
    private Long membersCapacityId;

    public CapacityNameType getCapacityNameType() {
        return capacityNameType;
    }

    public void setCapacityNameType(CapacityNameType capacityNameType) {
        this.capacityNameType = capacityNameType;
    }

    public List<Integer> getCapacities() {
        return capacities;
    }

    public void setCapacities(List<Integer> capacities) {
        this.capacities = capacities;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public Long getMembersCapacityId() {
        return membersCapacityId;
    }

    public void setMembersCapacityId(Long membersCapacityId) {
        this.membersCapacityId = membersCapacityId;
    }
}
