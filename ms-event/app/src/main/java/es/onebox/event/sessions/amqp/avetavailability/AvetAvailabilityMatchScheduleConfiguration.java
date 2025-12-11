package es.onebox.event.sessions.amqp.avetavailability;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvetAvailabilityMatchScheduleConfiguration {

    @Value("${amqp.avet-availability-match-schedule.name}")
    private String queueName;

    @Bean
    public DefaultProducer avetAvailabilityMatchScheduleProducer() {
        return new DefaultProducer(queueName);
    }
}
