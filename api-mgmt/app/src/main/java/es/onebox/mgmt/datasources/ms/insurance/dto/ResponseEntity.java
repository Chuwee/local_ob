package es.onebox.mgmt.datasources.ms.insurance.dto;

import java.io.Serial;
import java.io.Serializable;

public class ResponseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2620232586292557484L;

    private Integer id;
    private String name;

    public ResponseEntity() {
    }

    public ResponseEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
