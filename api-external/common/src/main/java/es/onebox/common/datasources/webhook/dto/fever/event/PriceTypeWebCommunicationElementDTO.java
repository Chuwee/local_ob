package es.onebox.common.datasources.webhook.dto.fever.event;

import java.io.Serial;
import java.io.Serializable;

public class PriceTypeWebCommunicationElementDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6409678949500328485L;

    private String type;
    private String lang;
    private String value;

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
