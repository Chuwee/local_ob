package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serializable;

public class VenueTemplateSector implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String code;
    private String color;
    private Long order;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }
}
