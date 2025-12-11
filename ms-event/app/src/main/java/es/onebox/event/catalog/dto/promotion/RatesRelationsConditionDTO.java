package es.onebox.event.catalog.dto.promotion;

import java.io.Serial;
import java.io.Serializable;

public class RatesRelationsConditionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 918052076722208389L;

    private Integer id;
    private String name;
    private Long quantity;

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

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
