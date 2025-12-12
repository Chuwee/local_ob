package es.onebox.ms.notification.ie.orderbookingpurchase;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderBookingPurchaseRoute extends RouteBuilder {

    @Autowired
    private OrderBookingPurchaseConfiguration orderBookingPurchaseConfiguration;

    @Autowired
    private OrderBookingPurchaseProcessor orderBookingPurchaseProcessor;

    @Override
    public void configure() {
        from(orderBookingPurchaseConfiguration.getRouteURL())
                .autoStartup(Boolean.FALSE)
                .id(orderBookingPurchaseConfiguration.getName())
                .process(orderBookingPurchaseProcessor);
    }

}
