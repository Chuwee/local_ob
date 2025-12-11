package es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice;

import java.io.Serial;
import java.io.Serializable;

public class DynamicPriceTranslation implements Serializable {

    @Serial
    private static final long serialVersionUID = 7571456957796706229L;

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
