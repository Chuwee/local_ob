package es.onebox.event.priceengine.simulation.domain;

import es.onebox.event.priceengine.simulation.domain.enums.PromotionType;

import java.io.Serial;
import java.io.Serializable;

public class BasePromotion implements Serializable {

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
