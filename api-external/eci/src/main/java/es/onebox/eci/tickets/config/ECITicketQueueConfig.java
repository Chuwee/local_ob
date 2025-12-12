package es.onebox.eci.tickets.config;

import es.onebox.eci.tickets.ECITicketProcessor;
import es.onebox.eci.tickets.ECITicketRoute;
import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.eci-ticket")
public class ECITicketQueueConfig extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer eciTicketProducer() {
        return new DefaultProducer(getName(), true);
    }

    @Bean
    public ECITicketProcessor ticketProcessor() {
        return new ECITicketProcessor();
    }

    @Bean
    public ECITicketRoute ticketRoute() {
        return new ECITicketRoute();
    }

}
