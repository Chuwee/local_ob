package es.onebox.mgmt.datasources.ms.ticket.dto;

import java.io.Serializable;
import java.util.List;

public class PassbookField implements Serializable {
    private String key;
    private String group;
    private List<String> label;
    private List<String> value;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
