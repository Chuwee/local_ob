package es.onebox.event.datasources.ms.venue.dto;

public class SectorMapCapacitiesDTO extends SectorDTO {

    private static final long serialVersionUID = 1L;

    private Integer venueTemplateId;
    private String description;
    private Integer type;
    private Long saveSequence;
    private Integer order;

    public Integer getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Integer venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getSaveSequence() {
        return saveSequence;
    }

    public void setSaveSequence(Long saveSequence) {
        this.saveSequence = saveSequence;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
