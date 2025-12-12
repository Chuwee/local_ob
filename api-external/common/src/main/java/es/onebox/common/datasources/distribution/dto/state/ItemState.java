package es.onebox.common.datasources.distribution.dto.state;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ItemState implements Serializable {

    @Serial
    private static final long serialVersionUID = -8800408852344128959L;

    @JsonProperty("type")
    private ItemStateType type;
    @JsonProperty("next_order")
    private RelatedOrder nextOrder;
    @JsonProperty("previous_order")
    private RelatedOrder previousOrder;

    public ItemStateType getType() {
        return type;
    }

    public void setType(ItemStateType type) {
        this.type = type;
    }

    public RelatedOrder getNextOrder() {
        return nextOrder;
    }

    public void setNextOrder(RelatedOrder nextOrder) {
        this.nextOrder = nextOrder;
    }

    public RelatedOrder getPreviousOrder() {
        return previousOrder;
    }

    public void setPreviousOrder(RelatedOrder previousOrder) {
        this.previousOrder = previousOrder;
    }
}
