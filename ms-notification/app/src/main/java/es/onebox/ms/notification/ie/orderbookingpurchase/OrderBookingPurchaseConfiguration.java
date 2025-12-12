package es.onebox.ms.notification.ie.orderbookingpurchase;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.ie-order-booking-purchase")
public class OrderBookingPurchaseConfiguration extends AbstractQueueConfiguration {

    @Bean
    public OrderBookingPurchaseRoute orderBookingPurchaseRoute() {
        return new OrderBookingPurchaseRoute();
    }

    @Bean
    public OrderBookingPurchaseProcessor orderBookingPurchaseProcessor() {
        return new OrderBookingPurchaseProcessor();
    }

    @Bean
    public OrderBookingPurchaseService orderBookingPurchaseService() {
       return new OrderBookingPurchaseService();
    }

}
