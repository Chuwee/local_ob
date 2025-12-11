package es.onebox.event.forms.domain;

import java.io.Serializable;
import java.util.Map;

public class FieldValue implements Serializable {

    private String value;
    private Map<String, String> label;
    private String unicode;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, String> getLabel() {
        return label;
    }

    public void setLabel(Map<String, String> label) {
        this.label = label;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }
} 