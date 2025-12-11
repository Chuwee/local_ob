package es.onebox.event.events.dao.record;

import java.io.Serializable;

public class CommElementRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idIdioma;
    private Integer idItem;
    private String languageCode;
    private String value;
    private Integer position;
    private String altText;

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getIdIdioma() {
        return idIdioma;
    }

    public void setIdIdioma(Integer idIdioma) {
        this.idIdioma = idIdioma;
    }

    public Integer getIdItem() {
        return idItem;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public Integer getPosition() {return position;    }

    public void setPosition(Integer position) {this.position = position;    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
}
