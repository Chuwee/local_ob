package es.onebox.event.seasontickets.dto;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SeasonTicketStatusResponseDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    private Integer seasonTicketId;

    private SeasonTicketInternalGenerationStatus generationStatus;

    private SeasonTicketStatusDTO status;

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

    public SeasonTicketStatusDTO getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketStatusDTO status) {
        this.status = status;
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
