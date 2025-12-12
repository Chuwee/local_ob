package es.onebox.ms.notification.externalnotifications.event;

import java.io.Serializable;

public class CommunicationElement implements Serializable{

    private Integer languageId;
    private String  value;

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
