package es.onebox.event.catalog.dto.venue.container;

import java.io.Serializable;

public abstract class VenueCommElement implements Serializable {

    private String type;
    private String lang;
    private String value;

    public VenueCommElement() {
    }

    public VenueCommElement(String type, String lang, String value) {
        this.type = type;
        this.lang = lang;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
