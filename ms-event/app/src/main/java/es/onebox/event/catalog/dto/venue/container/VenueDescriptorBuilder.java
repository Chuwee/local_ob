package es.onebox.event.catalog.dto.venue.container;

import es.onebox.event.catalog.dto.venue.container.pricetype.VenuePriceType;
import es.onebox.event.catalog.dto.venue.container.tier.VenueTier;

import java.util.List;

public final class VenueDescriptorBuilder {
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

    private VenueDescriptorBuilder() {
    }

    public static VenueDescriptorBuilder builder() {
        return new VenueDescriptorBuilder();
    }

    public VenueDescriptorBuilder venueConfigId(Integer venueConfigId) {
        this.venueConfigId = venueConfigId;
        return this;
    }

    public VenueDescriptorBuilder eventId(Integer eventId) {
        this.eventId = eventId;
        return this;
    }

    public VenueDescriptorBuilder name(String name) {
        this.name = name;
        return this;
    }

    public VenueDescriptorBuilder graphic(Boolean graphic) {
        this.graphic = graphic;
        return this;
    }

    public VenueDescriptorBuilder type (Integer type) {
        this.type = type;
        return this;
    }

    public VenueDescriptorBuilder priceTypes(List<VenuePriceType> priceTypes) {
        this.priceTypes = priceTypes;
        return this;
    }

    public VenueDescriptorBuilder sectors(List<VenueSector> sectors) {
        this.sectors = sectors;
        return this;
    }

    public VenueDescriptorBuilder containers(List<VenueContainerDTO> containers) {
        this.containers = containers;
        return this;
    }

    public VenueDescriptorBuilder quotas(List<VenueQuota> quotas) {
        this.quotas = quotas;
        return this;
    }

    public VenueDescriptorBuilder tiers(List<VenueTier> tiers) {
        this.tiers = tiers;
        return this;
    }

    public VenueDescriptor build() {
        VenueDescriptor venueDescriptor = new VenueDescriptor();
        venueDescriptor.setVenueConfigId(venueConfigId);
        venueDescriptor.setEventId(eventId);
        venueDescriptor.setName(name);
        venueDescriptor.setGraphic(graphic);
        venueDescriptor.setPriceTypes(priceTypes);
        venueDescriptor.setSectors(sectors);
        venueDescriptor.setContainers(containers);
        venueDescriptor.setQuotas(quotas);
        venueDescriptor.setTiers(tiers);
        venueDescriptor.setType(type);
        return venueDescriptor;
    }
}
