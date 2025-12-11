package es.onebox.event.datasources.ms.ticket.dto.occupation;


import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SessionOccupationVenueContainer implements Serializable {

    @Serial
    private static final long serialVersionUID = 7818325512646068495L;

    private Long id;
    private SessionOccupationDTO occupation;
    private List<SessionPriceZoneOccupationDTO> priceZones;
    private List<SectorOccupationDTO> sectors;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SessionOccupationDTO getOccupation() {
        return occupation;
    }

    public void setOccupation(SessionOccupationDTO occupation) {
        this.occupation = occupation;
    }

    public List<SessionPriceZoneOccupationDTO> getPriceZones() {
        return priceZones;
    }

    public void setPriceZones(List<SessionPriceZoneOccupationDTO> priceZones) {
        this.priceZones = priceZones;
    }

    public List<SectorOccupationDTO> getSectors() {
        return sectors;
    }

    public void setSectors(List<SectorOccupationDTO> sectors) {
        this.sectors = sectors;
    }
}
