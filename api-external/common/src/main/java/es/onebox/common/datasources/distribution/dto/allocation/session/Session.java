package es.onebox.common.datasources.distribution.dto.allocation.session;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class Session extends IdNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5105677584545864114L;

    private String reference;
    private Map<String, String> texts;
    private Map<String, String> images;
    private SessionDate date;

    public Session() {
        super();
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

    public SessionDate getDate() {
        return date;
    }

    public void setDate(SessionDate date) {
        this.date = date;
    }
}
