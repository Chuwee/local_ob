package es.onebox.event.catalog.dto.venue.container;

import es.onebox.couchbase.annotations.Id;
import es.onebox.event.catalog.dto.venue.container.pricetype.VenuePriceType;
import es.onebox.event.catalog.dto.venue.container.tier.VenueTier;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VenueDescriptor implements Serializable {

    @Serial
    private static final long serialVersionUID = 7097879390178645080L;

    @Id
    private Integer venueConfigId;
    private Integer eventId;
    private String name;
    private Boolean graphic;
    private Integer type;
    private List<VenuePriceType> priceTypes;
    private List<VenueSector> sectors;
    private List<VenueContainerDTO> containers;
    private List<VenueQuota> quotas;
    private List<VenueTier> tiers;

    public Integer getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Integer venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getGraphic() {
        return graphic;
    }

    public List<VenueSector> getSectors() {
        return sectors;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setSectors(List<VenueSector> sectors) {
        this.sectors = sectors;
    }

    public void setGraphic(Boolean graphic) {
        this.graphic = graphic;
    }

    public List<VenueContainerDTO> getContainers() {
        return containers;
    }

    public void setContainers(List<VenueContainerDTO> containers) {
        this.containers = containers;
    }

    public List<VenuePriceType> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<VenuePriceType> priceTypes) {
        this.priceTypes = priceTypes;
    }

    public List<VenueQuota> getQuotas() {
        return quotas;
    }

    public void setQuotas(List<VenueQuota> quotas) {
        this.quotas = quotas;
    }

    public List<VenueTier> getTiers() {
        return tiers;
    }

    public void setTiers(List<VenueTier> tiers) {
        this.tiers = tiers;
    }
}
