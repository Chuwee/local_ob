package es.onebox.internal.xmlsepa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketRequestDTO implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("season_ticket_id")
    private Long seasonTicketId;

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }
} 