package es.onebox.common.datasources.ms.event.dto.response.session.passbook;

import java.io.Serial;
import java.io.Serializable;

public class SessionPassbookCommElement implements Serializable {

    @Serial
    private static final long serialVersionUID = 8282616523823897555L;

    private String tag;
    private String language;
    private String value;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

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
