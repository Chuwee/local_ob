package es.onebox.atm.config;

import es.onebox.atm.tickets.eip.ATMTicketProcessor;
import es.onebox.atm.tickets.eip.ATMTicketRoute;
import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.atm-ticket")
public class ATMTicketQueueConfig extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer atmTicketProducer() {
        return new DefaultProducer(getName(), true);
    }

    @Bean
    public ATMTicketProcessor ticketProcessor() {
        return new ATMTicketProcessor();
    }

    @Bean
    public ATMTicketRoute ticketRoute() {
        return new ATMTicketRoute();
    }

}
