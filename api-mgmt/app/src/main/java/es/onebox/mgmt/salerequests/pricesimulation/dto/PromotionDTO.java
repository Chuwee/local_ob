package es.onebox.mgmt.salerequests.pricesimulation.dto;

import es.onebox.mgmt.salerequests.pricesimulation.dto.enums.PromotionTypeDTO;

import java.io.Serializable;

public class PromotionDTO implements Serializable {

    private static final long serialVersionUID = 4061094974431377457L;

    private Long id;
    private String name;
    private PromotionTypeDTO type;

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

    public PromotionTypeDTO getType() {
        return type;
    }

    public void setType(PromotionTypeDTO type) {
        this.type = type;
    }
}
