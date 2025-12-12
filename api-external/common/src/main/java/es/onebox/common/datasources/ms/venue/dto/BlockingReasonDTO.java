package es.onebox.common.datasources.ms.venue.dto;

import java.io.Serializable;

public class BlockingReasonDTO implements Serializable {
    private static final long serialVersionUID = 8095485424200067377L;

    private Long id;
    private String name;
    private String color;
    private Boolean isDefault;
//    private BlockingReasonCode code;


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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
