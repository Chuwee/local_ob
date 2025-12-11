package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;

public class SeasonTicketDatasourceStatus implements Serializable {
    private static final long serialVersionUID = -5093689997901198284L;

    private Integer seasonTicketId;

    private SeasonTicketInternalGenerationStatus generationStatus;

    private SeasonTicketStatus status;

    public Integer getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Integer seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public SeasonTicketInternalGenerationStatus getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(SeasonTicketInternalGenerationStatus generationStatus) {
        this.generationStatus = generationStatus;
    }

    public SeasonTicketStatus getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketStatus status) {
        this.status = status;
    }
}
