package es.onebox.event.datasources.ms.ticket.dto;

import java.io.Serializable;
import java.util.List;

public class PassbookField implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String key;
    private List<String> label;
    private List<String> value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}
