package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketGenerationStatus;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketStatusDTO;

import java.io.Serializable;

public class SeasonTicketStatusResponseDTO implements Serializable {
    private static final long serialVersionUID = 7020194565365037366L;

    @JsonProperty("generation_status")
    private SeasonTicketGenerationStatus generationStatus;

    @JsonProperty("season_ticket_id")
    private Integer seasonTicketId;

    @JsonProperty("status")
    private SeasonTicketStatusDTO status;

    public SeasonTicketGenerationStatus getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(SeasonTicketGenerationStatus generationStatus) {
        this.generationStatus = generationStatus;
    }

    public Integer getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Integer seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public SeasonTicketStatusDTO getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketStatusDTO status) {
        this.status = status;
    }
}
