package es.onebox.mgmt.b2b.publishing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.b2b.publishing.enums.TicketStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SeatDataDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("venue_name")
    private String venueName;
    @JsonProperty("sector_name")
    private String sectorName;
    @JsonProperty("row_name")
    private String rowName;
    @JsonProperty("seat_name")
    private String seatName;
    @JsonProperty("seat_id")
    private Long seatId;
    @JsonProperty("not_numbered_area_id")
    private Long notNumberedAreaId;
    @JsonProperty("not_numbered_area_name")
    private String notNumberedAreaName;
    @JsonProperty("status")
    private TicketStatus seatStatus;


    public String getVenueName() {
        return venueName;
    }
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getSectorName() {
        return sectorName;
    }
    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public String getRowName() {
        return rowName;
    }
    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public String getSeatName() {
        return seatName;
    }
    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public Long getSeatId() {
        return seatId;
    }
    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Long getNotNumberedAreaId() {
        return notNumberedAreaId;
    }

    public void setNotNumberedAreaId(Long notNumberedAreaId) {
        this.notNumberedAreaId = notNumberedAreaId;
    }

    public String getNotNumberedAreaName() {
        return notNumberedAreaName;
    }

    public void setNotNumberedAreaName(String notNumberedAreaName) {
        this.notNumberedAreaName = notNumberedAreaName;
    }

    public TicketStatus getSeatStatus() { return seatStatus; }
    public void setSeatStatus(TicketStatus seatStatus) { this.seatStatus = seatStatus; }
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
