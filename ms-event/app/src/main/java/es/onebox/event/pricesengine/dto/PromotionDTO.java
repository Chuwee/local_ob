package es.onebox.event.pricesengine.dto;

import es.onebox.event.pricesengine.dto.enums.PromotionType;

import java.io.Serial;
import java.io.Serializable;

public class PromotionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3200864181443113834L;

    private Long id;
    private String name;
    private PromotionType type;

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

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }
}
