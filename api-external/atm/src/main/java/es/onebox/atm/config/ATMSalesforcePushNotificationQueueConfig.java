package es.onebox.atm.config;

import es.onebox.atm.webhook.eip.ATMSalesforcePushNotificationProcessor;
import es.onebox.atm.webhook.eip.ATMSalesforcePushNotificationRoute;
import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.atm-salesforce-push-notification")
public class ATMSalesforcePushNotificationQueueConfig extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer atmSalesforcePushNotificationProducer() {
        return new DefaultProducer(getName(), true);
    }

    @Bean
    public ATMSalesforcePushNotificationProcessor atmSalesforcePushNotificationProcessor() {
        return new ATMSalesforcePushNotificationProcessor();
    }

    @Bean
    public ATMSalesforcePushNotificationRoute atmSalesforcePushNotificationRoute() {
        return new ATMSalesforcePushNotificationRoute();
    }

}
