package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serializable;

public class BlockingReason implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String color;
    private Boolean isDefault;
    private BlockingReasonCode code;

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

    public BlockingReasonCode getCode() {
        return code;
    }

    public void setCode(BlockingReasonCode code) {
        this.code = code;
    }
}
