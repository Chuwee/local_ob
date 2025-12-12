package es.onebox.ms.notification.ie.orderrefund;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderRefundRoute extends RouteBuilder {

    @Autowired
    private OrderRefundConfiguration orderRefundConfiguration;

    @Autowired
    private OrderRefundProcessor orderBookingPurchaseProcessor;

    @Override
    public void configure() {
        from(orderRefundConfiguration.getRouteURL())
                .autoStartup(Boolean.FALSE)
                .id(orderRefundConfiguration.getName())
                .process(orderBookingPurchaseProcessor);
    }

}
