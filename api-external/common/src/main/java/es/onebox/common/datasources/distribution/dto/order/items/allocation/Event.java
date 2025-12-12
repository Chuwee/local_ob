package es.onebox.common.datasources.distribution.dto.order.items.allocation;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.util.Map;

public class Event extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = -5576992920009671538L;
    private EventType type;
    private String reference;
    private IdNameDTO entity;
    private Map<String, String> texts;
    private Map<String, String> images;


    public Event() {
        super();
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public Map<String, String> getTexts() {
        return texts;
    }

    public void setTexts(Map<String, String> texts) {
        this.texts = texts;
    }

    public Map<String, String> getImages() {
        return images;
    }

    public void setImages(Map<String, String> images) {
        this.images = images;
    }
}
