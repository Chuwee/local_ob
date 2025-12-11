package es.onebox.event.datasources.ms.channel.dto.attributes;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelAttributes implements Serializable {

    @Serial
    private static final long serialVersionUID = -3742908666133744510L;

    private List<Integer> hiddenBillboardEvents;
    private List<Integer> customEventsOrder;
    private List<Integer> carouselEventsOrder;
    private List<Integer> extendedEvents;

    public List<Integer> getHiddenBillboardEvents() {
        return hiddenBillboardEvents;
    }

    public void setHiddenBillboardEvents(List<Integer> hiddenBillboardEvents) {
        this.hiddenBillboardEvents = hiddenBillboardEvents;
    }

    public List<Integer> getCustomEventsOrder() {
        return customEventsOrder;
    }

    public void setCustomEventsOrder(List<Integer> customEventsOrder) {
        this.customEventsOrder = customEventsOrder;
    }

    public List<Integer> getCarouselEventsOrder() {
        return carouselEventsOrder;
    }

    public void setCarouselEventsOrder(List<Integer> carouselEventsOrder) {
        this.carouselEventsOrder = carouselEventsOrder;
    }

    public List<Integer> getExtendedEvents() {
        return extendedEvents;
    }

    public void setExtendedEvents(List<Integer> extendedEvents) {
        this.extendedEvents = extendedEvents;
    }
}
