package es.onebox.mgmt.venues.dto;

import es.onebox.mgmt.venues.enums.BlockingReasonCodeDTO;

import java.io.Serializable;

public class BlockingReasonRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String color;
    private Boolean isDefault;
    private BlockingReasonCodeDTO code;

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

    public BlockingReasonCodeDTO getCode() {
        return code;
    }

    public void setCode(BlockingReasonCodeDTO code) {
        this.code = code;
    }
}

