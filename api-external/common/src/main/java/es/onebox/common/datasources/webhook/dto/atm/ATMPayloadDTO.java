package es.onebox.common.datasources.webhook.dto.atm;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.webhook.dto.OrderPayloadDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class ATMPayloadDTO extends OrderPayloadDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7922489243317370672L;

    @JsonProperty("order_detail")
    private HashMap order;

    public HashMap getOrder() {
        return order;
    }

    public void setOrder(HashMap order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
