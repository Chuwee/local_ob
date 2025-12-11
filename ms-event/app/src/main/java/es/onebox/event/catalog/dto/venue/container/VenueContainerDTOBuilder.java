package es.onebox.event.catalog.dto.venue.container;

import java.util.List;

public final class VenueContainerDTOBuilder {
    private Integer id;
    private Integer venueConfigId;
    private String name;
    private String description;
    private String svg;
    private Boolean root;
    private List<VenueContainerLink> links;
    private List<VenueContainerNnz> nnzs;

    private VenueContainerDTOBuilder() {
    }

    public static VenueContainerDTOBuilder builder() {
        return new VenueContainerDTOBuilder();
    }

    public VenueContainerDTOBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public VenueContainerDTOBuilder venueConfigId(Integer venueConfigId) {
        this.venueConfigId = venueConfigId;
        return this;
    }

    public VenueContainerDTOBuilder name(String name) {
        this.name = name;
        return this;
    }

    public VenueContainerDTOBuilder description(String description) {
        this.description = description;
        return this;
    }

    public VenueContainerDTOBuilder svg(String svg) {
        this.svg = svg;
        return this;
    }

    public VenueContainerDTOBuilder root(Boolean root) {
        this.root = root;
        return this;
    }

    public VenueContainerDTOBuilder links(List<VenueContainerLink> links) {
        this.links = links;
        return this;
    }

    public VenueContainerDTOBuilder nnzs(List<VenueContainerNnz> nnzs) {
        this.nnzs = nnzs;
        return this;
    }

    public VenueContainerDTO build() {
        VenueContainerDTO venueContainerDTO = new VenueContainerDTO();
        venueContainerDTO.setId(id);
        venueContainerDTO.setVenueConfigId(venueConfigId);
        venueContainerDTO.setName(name);
        venueContainerDTO.setDescription(description);
        venueContainerDTO.setSvg(svg);
        venueContainerDTO.setRoot(root);
        venueContainerDTO.setLinks(links);
        venueContainerDTO.setNnzs(nnzs);
        return venueContainerDTO;
    }
}
