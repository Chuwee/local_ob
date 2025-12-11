package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketGenerationStatus;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketStatusDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SeasonTicketSearchResultDTO extends BaseSeasonTicketDTO implements Serializable {

    private static final long serialVersionUID = -2148931397117445275L;

    private SeasonTicketStatusDTO status;
    @JsonProperty("allow_renewal")
    private Boolean allowRenewal;
    @JsonProperty("allow_change_seat")
    private Boolean allowChangeSeat;

    @JsonProperty("generation_status")
    private SeasonTicketGenerationStatus generationStatus;

    public SeasonTicketStatusDTO getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketStatusDTO status) {
        this.status = status;
    }

    public SeasonTicketGenerationStatus getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(SeasonTicketGenerationStatus generationStatus) {
        this.generationStatus = generationStatus;
    }

    public Boolean getAllowRenewal() {
        return allowRenewal;
    }

    public void setAllowRenewal(Boolean allowRenewal) {
        this.allowRenewal = allowRenewal;
    }

    public Boolean getAllowChangeSeat() {
        return allowChangeSeat;
    }

    public void setAllowChangeSeat(Boolean allowChangeSeat) {
        this.allowChangeSeat = allowChangeSeat;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
