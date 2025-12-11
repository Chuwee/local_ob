package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;

public class IdValueDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String value;

    public IdValueDTO() {
    }

    public IdValueDTO(Integer id) {
        this.id = id;
    }

    public IdValueDTO(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
