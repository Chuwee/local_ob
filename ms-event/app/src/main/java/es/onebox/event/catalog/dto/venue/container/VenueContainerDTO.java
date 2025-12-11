package es.onebox.event.catalog.dto.venue.container;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VenueContainerDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3987054782962302216L;

    private Integer id;
    private Integer venueConfigId;
    private String name;
    private String description;
    private String svg;
    private Boolean root;
    private List<VenueContainerLink> links;
    private List<VenueContainerNnz> nnzs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Integer venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }

    public Boolean getRoot() {
        return root;
    }

    public void setRoot(Boolean root) {
        this.root = root;
    }

    public List<VenueContainerLink> getLinks() {
        return links;
    }

    public void setLinks(List<VenueContainerLink> links) {
        this.links = links;
    }

    public List<VenueContainerNnz> getNnzs() {
        return nnzs;
    }

    public void setNnzs(List<VenueContainerNnz> nnzs) {
        this.nnzs = nnzs;
    }

}
