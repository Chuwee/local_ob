package es.onebox.mgmt.packs.dto.channels;


import es.onebox.mgmt.packs.enums.PackStatus;

import java.io.Serializable;

public class PackInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private PackStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PackStatus getStatus() {
        return status;
    }

    public void setStatus(PackStatus status) {
        this.status = status;
    }

}
