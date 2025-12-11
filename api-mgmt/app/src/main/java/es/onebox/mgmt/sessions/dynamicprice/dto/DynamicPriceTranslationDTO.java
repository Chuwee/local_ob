package es.onebox.mgmt.sessions.dynamicprice.dto;

import java.io.Serial;
import java.io.Serializable;

public class DynamicPriceTranslationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1582748407861326002L;

    private String language;
    private String value;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
