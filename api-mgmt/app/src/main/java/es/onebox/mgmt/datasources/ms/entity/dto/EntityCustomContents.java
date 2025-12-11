package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;

public class EntityCustomContents implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tag;

    private String value;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
