package es.onebox.common.datasources.distribution.dto.order;

import java.io.Serial;
import java.io.Serializable;

public class OrderEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = -3824420797794337170L;

    private DeliveryOptions deliveryOptions;
    private ChannelEventEntityDTO promoterInfo;

    public OrderEvent() {
    }

    public DeliveryOptions getDeliveryOptions() {
        return deliveryOptions;
    }

    public void setDeliveryOptions(DeliveryOptions deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }

    public ChannelEventEntityDTO getPromoterInfo() {
        return promoterInfo;
    }

    public void setPromoterInfo(ChannelEventEntityDTO promoterInfo) {
        this.promoterInfo = promoterInfo;
    }
}
