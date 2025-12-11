package es.onebox.event.catalog.elasticsearch.dto.attributes;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@CouchDocument
public class ChannelAttributesDTO implements Serializable {

    @Id(index = 1)
    private Integer channelId;
    private Carousel carousel;
    private EventsCustomisedOrder eventsCustomisedOrder;
    private List<Integer> hiddenBillboardEvents;
    private Map<Integer, List<Integer>> eventSaleRestrictions;
    private List<Integer> extendedEvents;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Carousel getCarousel() {
        return carousel;
    }

    public void setCarousel(Carousel carousel) {
        this.carousel = carousel;
    }

    public EventsCustomisedOrder getEventsCustomisedOrder() {
        return eventsCustomisedOrder;
    }

    public void setEventsCustomisedOrder(EventsCustomisedOrder eventsCustomisedOrder) {
        this.eventsCustomisedOrder = eventsCustomisedOrder;
    }


    public List<Integer> getHiddenBillboardEvents() {
        return hiddenBillboardEvents;
    }

    public void setHiddenBillboardEvents(List<Integer> hiddenBillboardEvents) {
        this.hiddenBillboardEvents = hiddenBillboardEvents;
    }

    public Map<Integer, List<Integer>> getEventSaleRestrictions() {
        return eventSaleRestrictions;
    }

    public void setEventSaleRestrictions(Map<Integer, List<Integer>> eventSaleRestrictions) {
        this.eventSaleRestrictions = eventSaleRestrictions;
    }

    public List<Integer> getExtendedEvents() {
        return extendedEvents;
    }

    public void setExtendedEvents(List<Integer> extendedEvents) {
        this.extendedEvents = extendedEvents;
    }
}
