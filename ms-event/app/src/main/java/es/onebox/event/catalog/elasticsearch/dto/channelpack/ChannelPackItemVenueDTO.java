package es.onebox.event.catalog.elasticsearch.dto.channelpack;

public class ChannelPackItemVenueDTO {

    private Long id;
    private String name;
    private ChannelPackItemVenueLocationDTO location;
    private String image;
    private String timeZone;
    private Boolean isGraphic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChannelPackItemVenueLocationDTO getLocation() {
        return location;
    }

    public void setLocation(ChannelPackItemVenueLocationDTO location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Boolean getGraphic() {
        return isGraphic;
    }

    public void setGraphic(Boolean graphic) {
        isGraphic = graphic;
    }
}

