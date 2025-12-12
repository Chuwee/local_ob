package es.onebox.atm.email.config;


import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PdfTicketGenerationConfiguration {

    @Bean
    public DefaultProducer pdfTicketGenerationProducer(@Value("${amqp.pdf-ticket-generation.name}") String queueName) {
        return new DefaultProducer(queueName, true);
    }

}
