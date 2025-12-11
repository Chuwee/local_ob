package es.onebox.event.promotions.dto;

import java.io.Serial;
import java.io.Serializable;

public class RatesRelationsCondition implements Serializable {

    @Serial
    private static final long serialVersionUID = 8101231443334579995L;

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
