package es.onebox.event.catalog.dto.venue.container;

public abstract class VenueContainerItem {

    private Long id;
    private Integer containerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getContainerId() {
        return containerId;
    }

    public void setContainerId(Integer containerId) {
        this.containerId = containerId;
    }

}
