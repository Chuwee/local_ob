package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;

public class IdValue implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String value;

    public IdValue(Long id) {
        this.id = id;
    }

    public IdValue() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
